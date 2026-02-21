package me.auxatlas.greencuts.mixin;

import me.auxatlas.greencuts.util.IPlantableBush;
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
    public abstract boolean canSurvive(BlockState state, LevelReader level, BlockPos pos);

    public MixinBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean greenCuts$canSurviveAtPos(BlockState block, LevelReader level, BlockPos pos) {
        return canSurvive(block, level, pos);
    }
}
