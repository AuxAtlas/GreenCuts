package me.auxatlas.greencuts.mixin;

import me.auxatlas.greencuts.Constants;
import me.auxatlas.greencuts.GreenCutsCommon;
import me.auxatlas.greencuts.util.GreenCutsUtils;
import me.auxatlas.greencuts.util.IAutoPlantable;
import me.auxatlas.greencuts.util.IPlantableBush;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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


        // Check if we are server-sided logic
        if(!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        // Check if we already tried planting this item stack entity
        if(getPlantingFailed())
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

        BlockState state = Block.byItem(getItem().getItem()).defaultBlockState();
        BlockPos plantPos = getOnPos().above();

        plantingTicks = 0;

        // Check if this item can be placed(planted) at its current block pos
        if (!plantableBush.greenCuts$canSurviveAtPos(state, serverLevel, plantPos)) {
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
            setPlantingFailed(true);
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
    void greenCuts$itemStackWriteData(ValueOutput val, CallbackInfo ci) {
        CompoundTag greencutsRootTag = new CompoundTag();
        greencutsRootTag.putBoolean(Constants.TAGNAME_PLANTING_FAILD, this.plantingFailed);
        greencutsRootTag.putInt(Constants.TAGNAME_PLANTING_TICKS, this.plantingTicks);
        val.store(Constants.MOD_ID, CompoundTag.CODEC, greencutsRootTag);
    }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    void greenCuts$itemStackReadData(ValueInput val, CallbackInfo ci) {
        CompoundTag greencutsRootTag = val.read(Constants.MOD_ID, CompoundTag.CODEC).orElse(new CompoundTag());
        this.plantingFailed = greencutsRootTag.getBoolean(Constants.TAGNAME_PLANTING_FAILD).orElse(false);
        this.plantingTicks = greencutsRootTag.getInt(Constants.TAGNAME_PLANTING_TICKS).orElse(0);
    }



    @Override
    public boolean getPlantingFailed() {
        return plantingFailed;
    }

    @Override
    public void setPlantingFailed(boolean value) {
        plantingFailed = value;
    }


}
