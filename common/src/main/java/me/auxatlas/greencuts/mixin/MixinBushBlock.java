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

    public MixinBushBlock(Properties properties) {
        super(properties);
    }
}
