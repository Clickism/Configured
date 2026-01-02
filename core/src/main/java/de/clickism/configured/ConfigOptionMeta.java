/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import de.clickism.configured.comments.DefaultFormatter;
import de.clickism.configured.comments.HeaderFooter;
import org.jetbrains.annotations.Nullable;

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
     * Gets the description of the config option, formatted with the default value if applicable.
     *
     * @param value the default value to format into the description
     * @return the formatted description, or null if no description is set
     */
    public @Nullable String descriptionWithDefault(Object value) {
        if (defaultFormatter == null) {
            return description;
        }
        return defaultFormatter.format(description != null ? description : "", value);
    }

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
}
