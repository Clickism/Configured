/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import de.clickism.configured.comments.DefaultFormatter;
import de.clickism.configured.comments.HeaderFooter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Base class for config option metadata.
 *
 * @param <T> the type of the config option settings
 */
@SuppressWarnings("unchecked")
public abstract class ConfigOptionMeta<T> extends HeaderFooter<T> {
    private @Nullable String description;
    private @Nullable DefaultFormatter defaultFormatter;
    private boolean hidden;

    /**
     * Creates a new standalone ConfigOptionMeta instance.
     *
     * @return the new standalone ConfigOptionMeta instance
     */
    public static Standalone standalone() {
        return new Standalone() {};
    }

    /**
     * Formats the default value, handling collections.
     *
     * @param defaultValue the default value
     * @return the formatted default value
     */
    private static String formatDefaultValue(Object defaultValue) {
        if (defaultValue instanceof Collection<?> collection) {
            return "[" + collection.stream().map(ConfigOptionMeta::formatDefaultValue).collect(Collectors.joining(", ")) + "]";
        }
        return String.valueOf(defaultValue);
    }

    /**
     * Copies metadata from another ConfigOptionMeta instance.
     *
     * @param other the other ConfigOptionMeta instance
     * @return this ConfigOptionMeta instance
     */
    public ConfigOptionMeta<?> copyFrom(ConfigOptionMeta<?> other) {
        this.description = other.description;
        this.defaultFormatter = other.defaultFormatter;
        this.hidden = other.hidden;
        this.header(other.header());
        this.footer(other.footer());
        return this;
    }

    /**
     * Gets the description of the config option, formatted with the default value if applicable.
     *
     * @param value the default value to format into the description
     * @return the formatted description, or null if no description is set
     */
    public @Nullable String descriptionWithDefault(Object value) {
        if (defaultFormatter == null) {
            return description;
        }
        return defaultFormatter.format(description != null ? description : "", formatDefaultValue(value));
    }

    // TODO: Move to format-specific logic?

    /**
     * Sets the description of the config option.
     *
     * @param description the description to set
     * @return this config option
     */
    public T description(@Nullable String description) {
        if (description != null) {
            description = description.trim();
        }
        this.description = description;
        return (T) this;
    }

    /**
     * Appends a default formatter that uses block style.
     *
     * @return this config option
     */
    public T appendDefault() {
        return appendDefault(DefaultFormatter.block());
    }

    /**
     * Appends a default formatter.
     *
     * @param formatter the default formatter to append
     * @return this config option
     */
    public T appendDefault(DefaultFormatter formatter) {
        this.defaultFormatter = formatter;
        return (T) this;
    }

    /**
     * Checks if the config option is hidden.
     * See {@link #hidden(boolean)} for details.
     *
     * @return if the config option is hidden
     */
    public boolean hidden() {
        return hidden;
    }

    /**
     * Marks this config option as hidden.
     * <p>
     * Hidden config options will be loaded, but their default values will not be
     * written to the config file by default.
     *
     * @return this config option
     */
    public T hidden(boolean hidden) {
        this.hidden = hidden;
        return (T) this;
    }

    /**
     * Standalone subclass for ConfigOptionMeta.
     */
    public static class Standalone extends ConfigOptionMeta<Standalone> {}
}
