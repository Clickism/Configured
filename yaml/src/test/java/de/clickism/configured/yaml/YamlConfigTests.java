/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.yaml;

import de.clickism.configured.Caster;
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
        Config config = Config.of(file.getPath());
        config.option("enabled", true);
        config.option("list", List.of("a", "b", "c"))
                .withCaster(Caster.listOf(Caster.of(String.class)));
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
        Config config = Config.of(file.getPath());
        ConfigOption<Boolean> enabled = config.option("enabled", true);
        ConfigOption<List<String>> list = config.option("list", List.of("a", "b", "c"))
                .withCaster(Caster.listOf(Caster.of(String.class)));
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

        Config config = Config.of(file.getPath());
        ConfigOption<Boolean> enabled = config.option("enabled", false);
        ConfigOption<List<String>> list = config.option("list", List.of("x", "y", "z"))
                .withCaster(Caster.listOf(Caster.of(String.class)));

        config.load();

        assertTrue(config.get(enabled), "Enabled should be true");
        assertEquals(List.of("a", "b", "c"), config.get(list), "List should match loaded values");
    }
}
