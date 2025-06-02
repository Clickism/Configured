/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import org.jetbrains.annotations.Nullable;

/**
 * Config format provider for JSON and JSONC.
 */
public class JsonFormatProvider extends ConfigFormatProvider {
    @Override
    public @Nullable ConfigFormat getFormatFor(String extension) {
        return switch (extension) {
            case "json" -> JsonFormat.json();
            case "jsonc" -> JsonFormat.jsonc();
            default -> null;
        };
    }
}
