/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import de.clickism.configured.Config;
import de.clickism.configured.SaveLoadTests;

public class JsonCSaveLoadTests extends SaveLoadTests {
    @Override
    protected Config createConfig(String path) {
        return Config.of(path, JsonFormat.jsonc());
    }
}
