package de.clickism.configured.localization;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a localization key used for retrieving localized messages.
 * <p>
 * By default, the key is derived from {@link Object#toString()} and is converted
 * to lowercase with '$' replaced by '.'.
 */
public interface LocalizationKey {
    /**
     * Creates a new LocalizationKey with the specified key.
     * <p>
     * If you want to use parameters, use {@link #of(String, String...)} instead.
     *
     * @param key the key for the localization
     * @return a new LocalizationKey instance
     */
    static LocalizationKey of(@NotNull String key) {
        return new LocalizationKey() {
            @Override
            public @NotNull String key() {
                return key;
            }
        };
    }

    /**
     * Creates a new LocalizationKey with the specified key and parameters.
     *
     * @param key        the key for the localization
     * @param parameters the parameters to be used in the localization
     * @return a new LocalizationKey instance with parameters
     */
    static LocalizationKey of(@NotNull String key, @NotNull String... parameters) {
        return new LocalizationKey() {
            @Override
            public @NotNull String key() {
                return key;
            }

            @Override
            public String[] parameters() {
                return parameters;
            }
        };
    }

    /**
     * Returns the key for this localization.
     *
     * @return the key as a lowercase string with '$' replaced by '.'
     */
    default @NotNull String key() {
        return toString().toLowerCase().replace('$', '.');
    }

    /**
     * Returns the parameters associated with this localization key.
     * If no parameters are defined, an empty array is returned.
     *
     * <p>You can use the {@link Parameters} annotation to define parameters for a localization key.</p>
     *
     * @return an array of parameters
     */
    default String[] parameters() {
        return ParameterRegistry.getParameters(this);
    }
}
