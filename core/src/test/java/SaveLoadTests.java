import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveLoadTests {
    private void assertSaveAndLoad(Config config) {
        ConfigOption<String> name = config.register(ConfigOption.of("name", "John Doe")
                .description("Test Description"));
        ConfigOption<Integer> age = config.register(ConfigOption.of("age", 5)
                .description("Test Description"));
        ConfigOption<Boolean> enabled = config.register(ConfigOption.of("enabled", true)
                .description("Test Description"));
        ConfigOption<Map<String, String>> map = config.optionOf("map",
                        Map.of("key", "value"))
                .description("Test Description");
        config.load();
        config.set(name, "Jane Doe");
        config.set(age, 10);
        config.set(enabled, false);
        config.set(map, Map.of("key", "value", "key2", "value2"));
        config.save();
        config.load();
        assertEquals("Jane Doe", config.get(name));
        assertEquals(10, config.get(age));
        assertEquals(false, config.get(enabled));
        assertEquals(Map.of("key", "value", "key2", "value2"), config.get(map));
    }

    @Test
    public void testSaveLoadYaml(@TempDir Path tempDir) {
        Config config = Config.ofYaml(tempDir.resolve("config.yml").toFile());
        assertSaveAndLoad(config);
    }
}
