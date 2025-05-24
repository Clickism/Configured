package me.clickism.configured.localization;

import org.jetbrains.annotations.NotNull;

public interface LocalizationKey {

    static LocalizationKey of(@NotNull String key) {
        return new LocalizationKey() {
            @Override
            public @NotNull String key() {
                return key;
            }
        };
    }

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

    default @NotNull String key() {
        return toString().toLowerCase().replace('$', '.');
    }

    default String[] parameters() {
        return ParameterRegistry.getParameters(this);
    }
}
