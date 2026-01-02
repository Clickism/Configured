/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

interface ConfiguredExample {
    Config CONFIG =
            Config.of("config.yml")
                    .version(1)
                    .appendDefaults()
                    .header("""
                            Example configuration file
                            Using "Configured"!
                            """);

    ConfigOption<String> NAME =
            CONFIG.option("name", "John Smith")
                    .description("Name of the user");

    ConfigOption<Integer> AGE =
            CONFIG.option("age", 18)
                    .description("Age of the user")
                    .onChange(value -> {
                        System.out.println("Age changed to: " + value);
                    });

    ConfigOption<Boolean> STUDENT =
            CONFIG.option("student", false)
                    .description("Whether the user is a student or not");

    static void main(String[] args) {
        CONFIG.load();
        String name = NAME.get();
        int age = AGE.get();
        boolean student = STUDENT.get();
    }
}
