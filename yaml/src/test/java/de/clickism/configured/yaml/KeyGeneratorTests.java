/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.yaml;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.KeyGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyGeneratorTests {
    @Test
    public void testOldKeys(@TempDir Path tempDir) throws Exception {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.of(path.toString())
                .keyGenerator(KeyGenerator.withAlternative(key -> key.replace('_', '-')))
                .version(1);

        Files.writeString(path, """
                old-key: 5
                new-key2: 10
                """);

        ConfigOption<Integer> oldKey = config.option("new_key", 10)
                .alternativeKey("old-key");

        ConfigOption<Integer> generatedKey = config.option("new_key2", 20);
        config.load();
        assertEquals(5, config.get(oldKey));
        assertEquals(10, config.get(generatedKey));
    }
}
