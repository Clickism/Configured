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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class YamlSaveLoadTests {
    @Test
    public void testSaveAndLoad(@TempDir Path tempDir) {
        Config config = Config.of(tempDir.resolve("config.yml").toString());
        ConfigOption<String> stringValue = config.optionOf("stringValue", "Test String")
                .description("Test Description");
        ConfigOption<Integer> integerValue = config.optionOf("integerValue", 5)
                .description("Test Description");
        ConfigOption<Boolean> booleanValue = config.optionOf("booleanValue", true)
                .description("Test Description");
        ConfigOption<Double> doubleValue = config.optionOf("doubleValue", 1.23)
                .description("Test Description");
        ConfigOption<Float> floatValue = config.optionOf("floatValue", 4.56f)
                .description("Test Description");
        ConfigOption<Long> longValue = config.optionOf("longValue", 123456789L)
                .description("Test Description");
        ConfigOption<Short> shortValue = config.optionOf("shortValue", (short) 42)
                .description("Test Description");
        ConfigOption<Byte> byteValue = config.optionOf("byteValue", (byte) 7)
                .description("Test Description");
        ConfigOption<Character> charValue = config.optionOf("charValue", 'A')
                .description("Test Description");
        ConfigOption<Map<String, String>> map = config.optionOf("map", Map.of("key", "value"), String.class, String.class)
                .description("Test Description");
        ConfigOption<List<String>> list = config.optionOf("list", List.of("a", "b", "c"), String.class)
                .description("Test Description");
        ConfigOption<Set<Integer>> set = config.optionOf("set", Set.of(1, 2, 3), Integer.class)
                .description("Test Description");
        config.load();
        config.set(stringValue, "Jane Doe")
                .set(integerValue, 10)
                .set(booleanValue, false)
                .set(doubleValue, 9.87)
                .set(floatValue, 6.54f)
                .set(longValue, 987654321L)
                .set(shortValue, (short) 24)
                .set(byteValue, (byte) 3)
                .set(charValue, 'Z')
                .set(map, Map.of("key", "value", "key2", "value2"))
                .set(list, List.of("x", "y", "z"))
                .set(set, Set.of(4, 5, 6));
        // Save and reload config
        config.save();
        config.load();
        assertEquals("Jane Doe", config.get(stringValue));
        assertEquals(10, config.get(integerValue));
        assertEquals(false, config.get(booleanValue));
        assertEquals(9.87, config.get(doubleValue));
        assertEquals(6.54f, config.get(floatValue));
        assertEquals(987654321L, config.get(longValue));
        assertEquals((short) 24, config.get(shortValue));
        assertEquals((byte) 3, config.get(byteValue));
        assertEquals('Z', config.get(charValue));
        assertEquals(Map.of("key", "value", "key2", "value2"), config.get(map));
        assertIterableEquals(List.of("x", "y", "z"), config.get(list));
        assertIterableEquals(Set.of(4, 5, 6), config.get(set));
    }
}
