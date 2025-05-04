package me.clickism.configured;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

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

    private static String formatDefaultValue(Object defaultValue) {
        if (defaultValue instanceof List<?> list) {
            return "[" +
                   list.stream()
                           .map(ConfigOption::formatDefaultValue)
                           .collect(Collectors.joining(", "))
                   + "]";
        }
        return String.valueOf(defaultValue);
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
        this.description = description.trim();
        return this;
    }

    /**
     * Appends the default value to the current description of the config option.
     *
     * @return this config option
     */
    public ConfigOption<T> appendDefaultValue() {
        if (description != null) {
            description += "\n";
        }
        appendDefaultValueInternal();
        return this;
    }

    /**
     * Appends the default value to the current description of the config option inlined,
     * without a new line.
     *
     * @return this config option
     */
    public ConfigOption<T> appendInlinedDefaultValue() {
        if (description != null) {
            description += " ";
        }
        appendDefaultValueInternal();
        return this;
    }

    /**
     * Appends the default value to the current description of the config option in parentheses
     * and inlined, without a new line.
     *
     * @return this config option
     */
    public ConfigOption<T> appendParenthesizedDefaultValue() {
        if (description != null) {
            description += " ";
        }
        description += "(";
        appendDefaultValueInternal();
        description += ")";
        return this;
    }

    private void appendDefaultValueInternal() {
        String string = description == null ? "" : description;
        description = string + "Default: " + formatDefaultValue(defaultValue);
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
        this.header = header.trim();
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
        this.footer = footer.trim();
        return this;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
