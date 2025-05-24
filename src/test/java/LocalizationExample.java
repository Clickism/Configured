import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import me.clickism.configured.localization.Localization;
import me.clickism.configured.localization.LocalizationKey;
import me.clickism.configured.localization.Parameters;

enum Message implements LocalizationKey {
    USER_NOT_FOUND,
    CONFIGURATION_ERROR,
    INVALID_INPUT,
    @Parameters({"player", "action"})
    OPERATION_SUCCESS,
    @Parameters({"reason", "details"})
    OPERATION_FAILED,
    @Parameters({"player"})
    MAJOR_FREAK
}

public class LocalizationExample {

    public static final Localization LOCALIZATION = Localization.ofYaml(lang -> lang + ".yml")
            .version(3)
            .fallbackLanguage("en_US")
            .registerKeysFor(Message.class);

    public static final ConfigOption<String> LANGUAGE = ConfigOption.of("language", "en_US")
            .onLoad(language -> LOCALIZATION.language(language).load());

    private static final LocalizationKey PLAYER_JOINED = LocalizationKey.of("player_joined", "player");

    public static void main(String[] args) {
        LOCALIZATION.registerKey(PLAYER_JOINED);

        Config config = Config.ofYaml("config.yml");
        config.register(LANGUAGE);
        config.load();


        // Example usage of localization
        String operationSuccessMessage = LOCALIZATION.get(Message.OPERATION_SUCCESS, "Clickism", "created a new config");
        System.out.println(operationSuccessMessage);

        String operationFailedMessage = LOCALIZATION.get(Message.OPERATION_FAILED, "Invalid data", "Data does not match expected format");
        System.out.println(operationFailedMessage);

        String playerJoinedMessage = LOCALIZATION.get(PLAYER_JOINED, "Clickismsssmsms");
        System.out.println(playerJoinedMessage);
    }
}
