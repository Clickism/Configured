package me.clickism.configured;

import org.jetbrains.annotations.Nullable;

public abstract class ConfigOption<T> {
    private final String key;
    private final T defaultValue;
    private @Nullable String description;

    protected ConfigOption(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String key() {
        return key;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public @Nullable String description() {
        return description;
    }

    public ConfigOption<T> description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public static <T> ConfigOption<T> of(String key, T defaultValue) {
        return new ConfigOption<>(key, defaultValue) {};
    }
}
