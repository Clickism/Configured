import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTests {
    @Test
    public void testHeader(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.ofYaml(path.toFile());
        config.header("""
                HEADER
                ------
                This is a header comment
                """);
        config.save();

        String string = Files.readString(path);
        assertEquals("""
                # HEADER
                # ------
                # This is a header comment
                
                {}
                """, string);
    }

    @Test
    public void testFooter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.ofYaml(path.toFile());
        config.footer("""
                FOOTER
                ------
                This is a footer comment
                """);
        config.save();

        String string = Files.readString(path);
        assertEquals("""
                {}
                
                # FOOTER
                # ------
                # This is a footer comment
                """, string);
    }

    @Test
    public void testHeaderAndFooter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.ofYaml(path.toFile());
        config.header("""
                HEADER
                ------
                This is a header comment
                """);
        config.footer("""
                FOOTER
                ------
                This is a footer comment
                """);
        config.register(ConfigOption.of("test", 5).description("Test value\nDefault: 5"));
        config.save();

        String string = Files.readString(path);
        assertEquals("""
                # HEADER
                # ------
                # This is a header comment
                
                # Test value
                # Default: 5
                test: 5
                
                # FOOTER
                # ------
                # This is a footer comment
                """, string);
    }

    @Test
    public void testOptionHeaderAndFooter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.ofYaml(path.toFile());
        config.register(ConfigOption.of("name", "Hello")
                .description("Name of the player"));
        config.register(ConfigOption.of("test", 5)
                .header("Test header")
                .description("Test value\nDefault: 5")
                .footer("Test footer"));
        config.register(ConfigOption.of("enabled", true));
        config.save();

        String string = Files.readString(path);
        assertEquals("""
                # Name of the player
                name: Hello
                
                # Test header
                
                # Test value
                # Default: 5
                test: 5
                
                # Test footer
                
                enabled: true
                """, string);
    }

    @Test
    public void testAppendDefaultValue(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.ofYaml(path.toFile());
        config.register(ConfigOption.of("test", 5)
                .description("Test value")
                .appendDefaultValue());
        config.register(ConfigOption.of("name", "Player")
                .appendDefaultValue());
        config.register(ConfigOption.of("enabled", true)
                .description("Boolean value.")
                .appendInlinedDefaultValue());
        config.register(ConfigOption.of("pi", 3.14)
                .description("Pi constant")
                .appendParenthesizedDefaultValue());
        config.register(ConfigOption.of("list", List.of("a", "b", "c"))
                .appendDefaultValue());
        config.save();

        String string = Files.readString(path);
        assertEquals(
                """
                        # Test value
                        # Default: 5
                        test: 5
                        
                        # Default: Player
                        name: Player
                        
                        # Boolean value. Default: true
                        enabled: true
                        
                        # Pi constant (Default: 3.14)
                        pi: 3.14
                        
                        # Default: [a, b, c]
                        list: [a, b, c]
                        """, string
        );

    }
}
