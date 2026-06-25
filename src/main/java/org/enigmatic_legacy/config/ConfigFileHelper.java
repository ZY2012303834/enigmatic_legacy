package org.enigmatic_legacy.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.toml.TomlWriter;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class ConfigFileHelper {
    private ConfigFileHelper() {
    }

    public static void ensureDefaultConfig(String fileName, ModConfigSpec spec) {
        Path path = FMLPaths.CONFIGDIR.get().resolve(fileName);

        if (Files.exists(path)) {
            return;
        }

        try {
            Files.createDirectories(path.getParent());

            CommentedConfig config = new SynchronizedConfig(
                    InMemoryCommentedFormat.defaultInstance(),
                    LinkedHashMap::new
            );

            spec.correct(config);

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                new TomlWriter().write(config, writer);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to create default config file: " + path, exception);
        }
    }
}
