package me.auxatlas.greencuts.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class GreenCutsConfig {
    @Setting(value = "mod_enabled")
    @Comment("Toggles the entire mod on/off")
    public boolean enabled = true;

    @Setting(value = "auto_plant_delay")
    @Comment("Delay before attempting to auto-plant a sapling in ticks (1 second is 20 ticks)")
    public int autoPlantDelay = 200;

    @Setting(value = "auto_plant_chance")
    @Comment("Percent chance for a sapling to actually auto-plant once the delay completed. Between 1 and 100")
    public int autoPlantChance = 66;
}
