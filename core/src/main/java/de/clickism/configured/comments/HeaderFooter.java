/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.comments;

import org.jetbrains.annotations.Nullable;

/**
 * Base class for entities that can have header and footer comments.
 *
 * @param <T> the type of the subclass
 */
@SuppressWarnings("unchecked")
public abstract class HeaderFooter<T> {
    private @Nullable String header;
    private @Nullable String footer;

    /**
     * Constructor for HeaderFooter.
     */
    public HeaderFooter() {
    }

    /**
     * Gets the header comment.
     *
     * @return the header comment
     */
    public @Nullable String header() {
        return header;
    }

    /**
     * Sets the header comment.
     *
     * @param header the header comment
     * @return this
     */
    public T header(@Nullable String header) {
        if (header != null) {
            header = header.trim();
        }
        this.header = header;
        return (T) this;
    }

    /**
     * Gets the footer comment.
     *
     * @return the footer comment
     */
    public @Nullable String footer() {
        return footer;
    }

    /**
     * Sets the footer comment.
     *
     * @param footer the footer comment
     * @return this
     */
    public T footer(@Nullable String footer) {
        if (footer != null) {
            footer = footer.trim();
        }
        this.footer = footer;
        return (T) this;
    }
}
