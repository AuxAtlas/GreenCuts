package me.auxjackdev.greencuts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlantableBush {
    public boolean canSurviveAtPos(BlockState block, ServerLevel serverLevel, BlockPos pos);
}
