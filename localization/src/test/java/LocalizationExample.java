import me.clickism.configured.Configured;
import me.clickism.configured.format.JsonFormat;
import me.clickism.configured.format.YamlFormat;
import me.clickism.configured.localization.Localization;
import me.clickism.configured.localization.LocalizationKey;
import me.clickism.configured.localization.Parameters;

enum Message implements LocalizationKey {
    @Parameters("username")
    USER_NOT_FOUND,
    CONFIGURATION_ERROR,
    INVALID_INPUT,
    @Parameters({"player", "action"})
    OPERATION_SUCCESS,
    @Parameters({"reason", "details"})
    OPERATION_FAILED,
}

public class LocalizationExample {

    public static final Localization LOCALIZATION =
            Localization.of(YamlFormat.yaml(), lang -> lang + ".json")
                    .resourceProvider(Configured.class, lang -> "/" + lang + ".json")
                    .version(2)
                    .fallbackLanguage("en_US")
                    .language("en_US");

    public static void main(String[] args) {
        LOCALIZATION.load();
        System.out.println(LOCALIZATION.get(Message.OPERATION_SUCCESS, "Clickism", "created a new config"));
        System.out.println(LOCALIZATION.get(Message.OPERATION_FAILED, "Invalid data", "Data does not match expected format"));
    }
}
