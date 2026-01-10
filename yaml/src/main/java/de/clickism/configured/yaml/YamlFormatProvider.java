/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.yaml;

import de.clickism.configured.format.ConfigFormat;
import de.clickism.configured.format.ConfigFormatProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Config yaml provider for YAML.
 */
public class YamlFormatProvider extends ConfigFormatProvider {
    @Override
    public @Nullable ConfigFormat getFormatFor(String extension) {
        return switch (extension) {
            case "yaml", "yml" -> YamlFormat.yaml();
            default -> null;
        };
    }
}
