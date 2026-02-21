package me.auxatlas.greencuts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlantableBush {
    public boolean greenCuts$canSurviveAtPos(BlockState block, LevelReader level, BlockPos pos);
}
