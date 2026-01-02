/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.localization;

/**
 * Interface for translatable localization keys.
 * <p>
 * Stores a reference to the {@link Localization} instance to retrieve the localized string.
 */
public interface Translatable extends LocalizationKey {
    /**
     * Get the Localization instance associated with this translatable key.
     *
     * @return the Localization instance
     */
    Localization localization();

    /**
     * Get the localized string for this key with optional arguments.
     *
     * @param args the arguments to format the localized string
     * @return the localized string
     */
    default String get(Object... args) {
        return localization().get(this, args);
    }
}
