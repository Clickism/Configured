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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OldKeyTests {
    @Test
    public void testOldKeys(@TempDir Path tempDir) throws Exception {
        Config config = Config.of(tempDir.resolve("config.yml").toString())
                .oldKeyGenerator(key -> key.replace('_', '-'))
                .version(1);

        Files.writeString(config.file().toPath(), """
                old-key: 5
                new-key2: 10
                """);

        ConfigOption<Integer> oldKey = config.optionOf("new_key", 10)
                        .oldKey("old-key");

        ConfigOption<Integer> generatedKey = config.optionOf("new_key2", 20);
        config.load();
        assertEquals(5, config.get(oldKey));
        assertEquals(10, config.get(generatedKey));
    }
}
