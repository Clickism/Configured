import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;

public class ConfiguredExample {
    public static final Config CONFIG = Config.ofYaml("config.yml")
            .version(1)
            .header("""
                    Example configuration file
                    Using "Configured"!
                    """);

    public static final ConfigOption<String> NAME = CONFIG.register(
            ConfigOption.of("name", "John Smith")
                    .description("Name of the user")
                    .appendDefaultValue()
    );

    public static final ConfigOption<Integer> AGE = CONFIG.register(
            ConfigOption.of("age", 18)
                    .description("Age of the user")
                    .appendDefaultValue()
    );

    public static final ConfigOption<Boolean> STUDENT = CONFIG.register(
            ConfigOption.of("student", true)
                    .description("Whether the user is a student or not")
                    .appendDefaultValue()
    );

    public static void main(String[] args) {
        CONFIG.load();
    }
}
