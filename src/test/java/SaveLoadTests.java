import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveLoadTests {
    private void assertSaveAndLoad(Config config) {
        ConfigOption<String> name = config.register(ConfigOption.of("name", "John Doe"));
        ConfigOption<Integer> age = config.register(ConfigOption.of("age", 5));
        ConfigOption<Boolean> enabled = config.register(ConfigOption.of("enabled", true));
        config.load();
        config.set(name, "Jane Doe");
        config.set(age, 10);
        config.set(enabled, false);
        config.save();
        config.load();
        assertEquals("Jane Doe", config.get(name));
        assertEquals(10, config.get(age));
        assertEquals(false, config.get(enabled));
    }

    @Test
    public void testSaveLoadYaml(@TempDir Path tempDir) {
        Config config = Config.ofYaml(tempDir.resolve("config.yml").toFile());
        assertSaveAndLoad(config);
    }

    @Test
    public void testSaveLoadJson(@TempDir Path tempDir) {
        Config config = Config.ofJson(tempDir.resolve("config.json").toFile());
        assertSaveAndLoad(config);
    }

    @Test
    public void testSaveLoadJsonWithComments(@TempDir Path tempDir) {
        Config config = Config.ofJsonWithComments(tempDir.resolve("config.jsonc").toFile());
        assertSaveAndLoad(config);
    }

    @Test
    public void testSaveLoadJson5(@TempDir Path tempDir) {
        Config config = Config.ofJson5(tempDir.resolve("config.json5").toFile());
        assertSaveAndLoad(config);
    }
}
