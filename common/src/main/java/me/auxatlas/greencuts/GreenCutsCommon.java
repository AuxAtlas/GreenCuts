package me.auxatlas.greencuts;

import me.auxatlas.greencuts.config.GreenCutsConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class GreenCutsCommon {

    private static final HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder()
            .path(Path.of("./config/greencuts.conf"))
            .prettyPrinting(true)
            .build();
    private static GreenCutsConfig _config= null;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        GreenCutsConfig tmp = getConfig();
        saveConfig();

        Constants.LOG.debug("[GreenCuts] Init!");
    }

    public static GreenCutsConfig getConfig() {
        if(_config != null)
            return _config;

        try {
            CommentedConfigurationNode rootNode = configLoader.load();

            _config = rootNode.get(GreenCutsConfig.class);
        } catch (ConfigurateException e) {
            Constants.LOG.error("[ERROR] Failed to load greencuts config file.");
        }

        return _config;
    }
    public static void saveConfig() {
        CommentedConfigurationNode rootNode = CommentedConfigurationNode.root();

        try {
            rootNode.set(GreenCutsConfig.class, _config);
            configLoader.save(rootNode);
        } catch (ConfigurateException ex) {
            Constants.LOG.error("[ERROR] Failed to save greencuts config.");
        }
    }
}