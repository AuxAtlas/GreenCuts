package me.auxatlas.greencuts;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class GreenCutsForge {
    public GreenCutsForge() {

        // This method is invoked by theForge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        GreenCutsCommon.init();
    }
}
