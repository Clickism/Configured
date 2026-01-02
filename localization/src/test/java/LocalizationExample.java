/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

import de.clickism.configured.Configured;
import de.clickism.configured.localization.Localization;
import de.clickism.configured.localization.Parameters;
import de.clickism.configured.localization.Translatable;

enum Message implements Translatable {
    @Parameters("username")
    USER_NOT_FOUND,
    CONFIGURATION_ERROR,
    INVALID_INPUT,
    @Parameters({"player", "action"})
    OPERATION_SUCCESS,
    @Parameters({"reason", "details"})
    OPERATION_FAILED;

    public static final Localization LOCALIZATION =
            Localization.of(lang -> lang + ".yml")
                    .resourceProvider(Configured.class, lang -> "/" + lang + ".yml")
                    .version(2)
                    .fallbackLanguage("en_US")
                    .language("en_US");

    @Override
    public Localization localization() {
        return LOCALIZATION;
    }
}

public class LocalizationExample {

    public static void main(String[] args) {
        Message.LOCALIZATION.load();
        System.out.println(Message.OPERATION_SUCCESS.get("Clickism", "created a new config"));
        System.out.println(Message.OPERATION_SUCCESS.get("Invalid data", "Data does not match expected format"));
    }
}
