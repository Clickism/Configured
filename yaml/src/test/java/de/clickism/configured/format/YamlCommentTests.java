package de.clickism.configured.format;

import de.clickism.configured.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlCommentTests {
    @Test
    public void testHeader(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.of(path.toFile());
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
                
                """, string);
    }

    @Test
    public void testFooter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.of(path.toFile());
        config.footer("""
                FOOTER
                ------
                This is a footer comment
                """);
        config.save();

        String string = Files.readString(path);
        assertEquals("""
                
                # FOOTER
                # ------
                # This is a footer comment
                """, string);
    }

    @Test
    public void testHeaderAndFooter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("config.yml");
        Config config = Config.of(path.toFile());
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
        config.optionOf("test", 5)
                .description("Test value")
                .appendDefaultValue();
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
        Config config = Config.of(path.toFile());
        config.optionOf("name", "Hello")
                .description("Name of the player");
        config.optionOf("test", 5)
                .header("Test header")
                .description("Test value\nDefault: 5")
                .footer("Test footer");
        config.optionOf("enabled", true);
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
        Config config = Config.of(path.toFile());
        config.optionOf("test", 5)
                .description("Test value")
                .appendDefaultValue();
        config.optionOf("name", "Player")
                .appendDefaultValue();
        config.optionOf("enabled", true)
                .description("""
                        Boolean value.
                        """)
                .appendInlinedDefaultValue();
        config.optionOf("pi", 3.14)
                .description("Pi constant")
                .appendParenthesizedDefaultValue();
        config.optionOf("list", List.of("a", "b", "c"), String.class)
                .appendDefaultValue();
        config.optionOf("map", Map.of("key", "value"), String.class, String.class)
                .description("Test Description");
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
                        list:
                        - a
                        - b
                        - c
                        
                        # Test Description
                        map:
                          key: value
                        """, string
        );
    }
}
