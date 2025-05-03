import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void testDefaultSave(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("config.yml").toFile();
        Config config = Config.ofYaml(file);
        ConfigOption<Boolean> enabled = config.register(ConfigOption.of("enabled", true));
        ConfigOption<List<String>> list = config.register(ConfigOption.of("list", List.of("a", "b", "c")));
        config.save();

        assertTrue(file.exists(), "Config file should exist after saving");
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals("enabled: true", lines.get(0));
        assertEquals("", lines.get(1));
        assertEquals("list: [a, b, c]", lines.get(2));
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

        Config config = Config.ofYaml(file);
        ConfigOption<Boolean> enabled = config.register(ConfigOption.of("enabled", false));
        ConfigOption<List<String>> list = config.register(ConfigOption.of("list", List.of("x", "y", "z")));
        config.load();

        assertTrue(config.get(enabled), "Enabled should be true");
        assertEquals(List.of("a", "b", "c"), config.get(list), "List should match loaded values");
    }
}
