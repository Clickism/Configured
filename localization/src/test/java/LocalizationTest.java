/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

import de.clickism.configured.localization.Localization;
import de.clickism.configured.localization.Parameters;
import de.clickism.configured.localization.Translatable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalizationTest {

    private static Localization localization;

    enum Message implements Translatable {
        @Parameters("username")
        USER_NOT_FOUND,
        CONFIGURATION_ERROR,
        INVALID_INPUT,
        @Parameters({"player", "action"})
        OPERATION_SUCCESS,
        @Parameters({"reason", "details"})
        OPERATION_FAILED;


        @Override
        public Localization localization() {
            return localization;
        }
    }

    @Test
    public void testLocalization(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("test.yml").toFile();
        localization =
                Localization.of(lang -> file.getAbsolutePath())
                        .version(2)
                        .fallbackLanguage("en_US")
                        .language("en_US");
        Files.writeString(file.toPath(), """
                _version: 2
                user_not_found: "User {username} could not be found."
                configuration_error: "There was an error in the configuration."
                invalid_input: "The input provided is invalid."
                operation_success: "{player} has successfully {action}."
                operation_failed: "Operation failed due to {reason}: {details}."
                """);
        localization.load();
        assertEquals(
                "User Alice could not be found.",
                Message.USER_NOT_FOUND.get("Alice")
        );
        assertEquals(
                "There was an error in the configuration.",
                Message.CONFIGURATION_ERROR.get()
        );
        assertEquals(
                "The input provided is invalid.",
                Message.INVALID_INPUT.get()
        );
        assertEquals(
                "Bob has successfully logged in.",
                Message.OPERATION_SUCCESS.get("Bob", "logged in")
        );
        assertEquals(
                "Operation failed due to network error: Connection timed out.",
                Message.OPERATION_FAILED.get("network error", "Connection timed out")
        );
    }
}
