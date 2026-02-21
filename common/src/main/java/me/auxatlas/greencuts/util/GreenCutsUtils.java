package me.auxatlas.greencuts.util;

import me.auxatlas.greencuts.GreenCutsCommon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;

public class GreenCutsUtils {
    public static boolean isSaplingStack(ItemStack stack) {
        return Block.byItem(stack.getItem()) instanceof SaplingBlock;
    }

    public static boolean runPlantingChance(ServerLevel serverLevel) {
        int plantChance = GreenCutsCommon.getConfig().autoPlantChance;
        return serverLevel.random.nextInt(101) < plantChance && plantChance > 0;
    }
}
