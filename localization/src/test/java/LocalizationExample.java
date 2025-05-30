/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

import de.clickism.configured.Configured;
import de.clickism.configured.localization.Localization;
import de.clickism.configured.localization.LocalizationKey;
import de.clickism.configured.localization.Parameters;

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
            Localization.of(lang -> lang + ".yml")
                    .resourceProvider(Configured.class, lang -> "/" + lang + ".yml")
                    .version(2)
                    .fallbackLanguage("en_US")
                    .language("en_US");

    public static void main(String[] args) {
        LOCALIZATION.load();
        System.out.println(LOCALIZATION.get(Message.OPERATION_SUCCESS, "Clickism", "created a new config"));
        System.out.println(LOCALIZATION.get(Message.OPERATION_FAILED, "Invalid data", "Data does not match expected format"));
    }
}
