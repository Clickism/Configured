/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

public class ConfiguredExample {
    public static final Config CONFIG =
            Config.of("config.yml")
                    .version(1)
                    .header("""
                            Example configuration file
                            Using "Configured"!
                            """);

    public static final ConfigOption<String> NAME =
            CONFIG.option("name", "John Smith")
                    .description("Name of the user")
                    .appendDefault();

    public static final ConfigOption<Integer> AGE =
            CONFIG.option("age", 18)
                    .description("Age of the user")
                    .appendDefault();

    public static final ConfigOption<Boolean> STUDENT =
            CONFIG.option("student", false)
                    .description("Whether the user is a student or not")
                    .appendDefault();

    public static void main(String[] args) {
        CONFIG.save();
    }
}
