package me.auxjackdev.greencuts.mixin;

import me.auxjackdev.greencuts.GreenCutsCommon;
import me.auxjackdev.greencuts.Constants;
import me.auxjackdev.greencuts.util.GreenCutsUtils;
import me.auxjackdev.greencuts.util.IAutoPlantable;
import me.auxjackdev.greencuts.util.IPlantableBush;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class MixinItemStack extends Entity implements IAutoPlantable {
    private boolean plantingFailed = false;
    private int plantingTicks = 0;


    public MixinItemStack(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int age;

    @Inject(method = "tick", at = @At("HEAD"))
    void greenCuts$itemStackTick(CallbackInfo ci) {
        // Check if this mod is enabled
        if(!GreenCutsCommon.getConfig().enabled)
            return;
        // Check if we are server-side logic
        if(level().isClientSide)
            return;
        // Check if we already tried planting this item stack entity
        if(greenCuts$getPlantingFailed())
            return;
        // Check if this item stack entity is a sapling item
        if (!GreenCutsUtils.isSaplingStack(getItem()))
            return;
        // Check if the item inherits IPlantableBush
        if (!(Block.byItem(getItem().getItem()) instanceof IPlantableBush plantableBush))
            return;

        plantingTicks++;
        // Check if this item stack has been on the ground long enough for us to try planting it
        if(plantingTicks < GreenCutsCommon.getConfig().autoPlantDelay)
            return;

        ServerLevel serverLevel = (ServerLevel) level();
        BlockState state = Block.byItem(getItem().getItem()).defaultBlockState();
        BlockPos plantPos = getOnPos().above();

        plantingTicks = 0;

        boolean validPlantPos = true;

        // Check if this item can be placed(planted) at its current block pos
        if (!plantableBush.greenCuts$canSurviveAtPos(state, serverLevel, plantPos)) {
            validPlantPos = false;
        }
        else {
            AABB blockBounds = new AABB(plantPos);
            List<Entity> foundEntities = serverLevel.getEntities(this, blockBounds, entity -> true);
            if(!foundEntities.isEmpty()) {
                validPlantPos = false;
            }
        }

        if(!validPlantPos) {
            if(!plantingFailed) {
                greenCuts$randomNudge();
            }
            return;
        }

        // Roll the dice to determine if planting was successful
        if (GreenCutsUtils.runPlantingChance(serverLevel)) {
            if(getItem().getCount() > 0) {
                serverLevel.setBlockAndUpdate(plantPos, state);
                greenCuts$randomNudge();
            }

            if (getItem().getCount() > 1) {
                // Decrement this item stack entity's stack count by 1
                getItem().setCount(getItem().getCount() - 1);
            }
            else {
                // We planted the final item in this stack. Discard this stack entity.
                getItem().setCount(0);
                discard();
            }

            return;
        }

        // If it fails two dice rolls in a row, then mark this item stack entity as a failed plant.
        if(!GreenCutsUtils.runPlantingChance(serverLevel)) {
            greenCuts$setPlantingFailed(true);
        }
    }

    @Unique
    void greenCuts$randomNudge() {
        double moveMultiplierX = level().random.nextIntBetweenInclusive(2,6);
        double moveMultiplierZ = level().random.nextIntBetweenInclusive(2,6);

        if(level().random.nextBoolean())
            moveMultiplierX *= -1;
        if(level().random.nextBoolean())
            moveMultiplierZ *= -1;

        // Nudge this item stack entity in a random X/Z direction after placing one so it may one day find a new BlockPos to be planted on
        setDeltaMovement(1d / moveMultiplierX, 1d, 1d / moveMultiplierZ);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    void greenCuts$itemStackWriteData(CompoundTag tag, CallbackInfo ci) {
        CompoundTag greencutsRootTag = new CompoundTag();
        greencutsRootTag.putBoolean(Constants.TAGNAME_PLANTING_FAILD, this.plantingFailed);
        greencutsRootTag.putInt(Constants.TAGNAME_PLANTING_TICKS, this.plantingTicks);
        tag.put(Constants.MOD_ID, greencutsRootTag);
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    void greenCuts$itemStackReadData(CompoundTag tag, CallbackInfo ci) {
        CompoundTag greencutsRootTag = tag.getCompound(Constants.MOD_ID);
        this.plantingFailed = greencutsRootTag.getBoolean(Constants.TAGNAME_PLANTING_FAILD);
        this.plantingTicks = greencutsRootTag.getInt(Constants.TAGNAME_PLANTING_TICKS);
    }



    @Override
    public boolean greenCuts$getPlantingFailed() {
        return plantingFailed;
    }

    @Override
    public void greenCuts$setPlantingFailed(boolean value) {
        plantingFailed = value;
    }


}
