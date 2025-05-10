package me.clickism.configured;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
    private final List<Consumer<T>> onLoadListeners = new ArrayList<>();
    private boolean hidden = false;
    private @Nullable String description;
    private @Nullable String header;
    private @Nullable String footer;

    // TODO: Auto-handle different collection types

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

    // TODO: Move to format-specific logic?
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
     * Marks the config option as hidden.
     * Hidden config options will be loaded, but their default values will not be
     * written to the config file by default.
     *
     * @return this config option
     */
    public ConfigOption<T> hidden() {
        this.hidden = true;
        return this;
    }

    /**
     * Checks if the config option is hidden.
     * Hidden config options will be loaded, but their default values will not be
     * written to the config file by default.
     *
     * @return true if the config option is hidden, false otherwise
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Adds a listener that will be called when the config option is loaded.
     *
     * @param listener the listener to add
     * @return this config option
     */
    public ConfigOption<T> onLoad(Consumer<T> listener) {
        onLoadListeners.add(listener);
        return this;
    }

    /**
     * Gets the list of listeners that will be called when the config option is loaded.
     *
     * @return the list of listeners
     */
    public List<Consumer<T>> onLoadListeners() {
        return onLoadListeners;
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
