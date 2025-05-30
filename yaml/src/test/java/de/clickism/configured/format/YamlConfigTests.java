/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlConfigTests {

    @Test
    public void testDefaultSave(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("config.yml").toFile();
        Config config = Config.of(file);
        config.optionOf("enabled", true);
        config.optionOf("list", List.of("a", "b", "c"), String.class);
        config.save();

        assertTrue(file.exists(), "Config file should exist after saving");
        String string = Files.readString(file.toPath());
        assertEquals("""
                enabled: true
                
                list:
                - a
                - b
                - c
                """, string);
    }

    @Test
    public void testSave(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("config.yml").toFile();
        Config config = Config.of(file);
        ConfigOption<Boolean> enabled = config.optionOf("enabled", true);
        ConfigOption<List<String>> list = config.optionOf("list", List.of("a", "b", "c"), String.class);
        config.set(enabled, false);
        config.set(list, List.of("d", "e", "f"));
        config.save();

        String string = Files.readString(file.toPath());
        assertEquals("""
                enabled: false
                
                list:
                - d
                - e
                - f
                """, string);
    }

    @Test
    public void testLoad(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("config.yml").toFile();
        Files.write(file.toPath(), List.of(
                "enabled: true",
                "",
                "list:",
                "  - a",
                "  - b",
                "  - c"
        ));

        Config config = Config.of(file);
        ConfigOption<Boolean> enabled = config.optionOf("enabled", false);
        ConfigOption<List<String>> list = config.optionOf("list", List.of("x", "y", "z"), String.class);
        config.load();

        assertTrue(config.get(enabled), "Enabled should be true");
        assertEquals(List.of("a", "b", "c"), config.get(list), "List should match loaded values");
    }
}
