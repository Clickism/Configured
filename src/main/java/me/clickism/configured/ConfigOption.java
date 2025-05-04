package me.clickism.configured;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a config option.
 * <p>
 * Overrides the {@link #hashCode()} method to return the hash code of the key.
 *
 * @param <T> the type of the config option
 */
public abstract class ConfigOption<T> {
    private final String key;
    private final T defaultValue;
    private @Nullable String description;

    private @Nullable String header;
    private @Nullable String footer;

    /**
     * Creates a new config option.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     */
    protected ConfigOption(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new config option with the given key and default value.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @param <T>          the type of the config option
     * @return the new config option
     */
    public static <T> ConfigOption<T> of(String key, T defaultValue) {
        return new ConfigOption<>(key, defaultValue) {};
    }

    /**
     * Gets the key of the config option.
     *
     * @return the key of the config option
     */
    public String key() {
        return key;
    }

    /**
     * Gets the default value of the config option.
     *
     * @return the default value of the config option
     */
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Gets the description of the config option.
     *
     * @return the description of the config option, or null if not set
     */
    public @Nullable String description() {
        return description;
    }

    /**
     * Sets the description of the config option.
     *
     * @param description the description of the config option
     * @return this config option
     */
    public ConfigOption<T> description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Gets the header of the config option.
     *
     * @return the header of the config option, or null if not set
     */
    public @Nullable String header() {
        return header;
    }

    /**
     * Sets the header of the config option.
     *
     * @param header the header of the config option
     * @return this config option
     */
    public ConfigOption<T> header(String header) {
        this.header = header;
        return this;
    }

    /**
     * Gets the footer of the config option.
     *
     * @return the footer of the config option, or null if not set
     */
    public @Nullable String footer() {
        return footer;
    }

    /**
     * Sets the footer of the config option.
     *
     * @param footer the footer of the config option
     * @return this config option
     */
    public ConfigOption<T> footer(String footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
