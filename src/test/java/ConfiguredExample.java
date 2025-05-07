import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;

public class ConfiguredExample {
    public static final Config CONFIG = Config.ofYaml("config.yml")
                    .version(1)
                    .header("""
                            Example configuration file
                            Using "Configured"!
                            """);

    public static final ConfigOption<String> NAME =
            CONFIG.optionOf("name", "John Smith")
                    .description("Name of the user")
                    .appendDefaultValue();

    public static final ConfigOption<Integer> AGE =
            CONFIG.optionOf("age", 18)
                    .description("Age of the user")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> STUDENT =
            CONFIG.optionOf("student", true)
                    .description("Whether the user is a student or not")
                    .appendDefaultValue();

    public static void main(String[] args) {
        CONFIG.save();
    }
}
