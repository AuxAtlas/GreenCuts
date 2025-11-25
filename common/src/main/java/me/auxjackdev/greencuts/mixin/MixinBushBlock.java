package me.auxjackdev.greencuts.mixin;

import me.auxjackdev.greencuts.util.IPlantableBush;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BushBlock.class)
public abstract class MixinBushBlock extends Block implements IPlantableBush {
    @Shadow
    protected abstract boolean canSurvive(BlockState state, LevelReader level, BlockPos pos);

    public MixinBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean greenCuts$canSurviveAtPos(BlockState state, ServerLevel serverLevel, BlockPos pos) {
        return canSurvive(state, serverLevel, pos);
    }

}
