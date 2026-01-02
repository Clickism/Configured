/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import de.clickism.configured.Caster;
import de.clickism.configured.Config;
import de.clickism.configured.comments.DefaultFormatter;
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
        Config config = Config.of(path.toFile().getPath());
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
        Config config = Config.of(path.toFile().getPath());
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
        Config config = Config.of(path.toFile().getPath());
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
        config.option("test", 5)
                .description("Test value")
                .appendDefault();
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
        Config config = Config.of(path.toFile().getPath());
        config.option("name", "Hello")
                .description("Name of the player");
        config.option("test", 5)
                .header("Test header")
                .description("Test value\nDefault: 5")
                .footer("Test footer");
        config.option("enabled", true);
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
        Config config = Config.of(path.toFile().getPath());
        config.option("test", 5)
                .description("Test value")
                .appendDefault();
        config.option("name", "Player")
                .appendDefault();
        config.option("enabled", true)
                .description("""
                        Boolean value.
                        """)
                .appendDefault(DefaultFormatter.inline(false));
        config.option("pi", 3.14)
                .description("Pi constant")
                .appendDefault(DefaultFormatter.inline(true));
        config.option("list", List.of("a", "b", "c"))
                .withCaster(Caster.listOf(Caster.of(String.class)))
                .appendDefault();
        config.option("map", Map.of("key", "value"))
                .withCaster(Caster.mapOf(Caster.of(String.class), Caster.of(String.class)))
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
