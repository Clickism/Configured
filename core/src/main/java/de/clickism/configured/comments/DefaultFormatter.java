/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.comments;

import org.jetbrains.annotations.NotNull;

/**
 * Formatter for default values in config option descriptions.
 */
@FunctionalInterface
public interface DefaultFormatter {
    // Formatters

    /**
     * Block formatter with <strong>"Default"</strong> prefix.
     *
     * @return the block formatter
     */
    static DefaultFormatter block() {
        return block("Default");
    }

    /**
     * Block formatter with custom prefix.
     *
     * @param prefix the prefix to use
     * @return the block formatter
     */
    static DefaultFormatter block(String prefix) {
        return (description, value) -> {
            String newLine = !description.isEmpty() ? "\n" : "";
            return description + newLine + prefix + ": " + value;
        };
    }

    /**
     * Inline formatter with <strong>"Default"</strong> prefix and parentheses.
     *
     * @return the inline formatter
     */
    static DefaultFormatter inline() {
        return inline("Default", true);
    }

    /**
     * Inline formatter with <strong>"Default"</strong> prefix.
     *
     * @param parentheses whether to use parentheses
     * @return the inline formatter
     */
    static DefaultFormatter inline(boolean parentheses) {
        return inline("Default", parentheses);
    }

    /**
     * Inline formatter with custom prefix.
     *
     * @param prefix      the prefix to use
     * @param parentheses whether to use parentheses
     * @return the inline formatter
     */
    static DefaultFormatter inline(String prefix, boolean parentheses) {
        return (description, value) -> {
            String space = !description.isEmpty() ? " " : "";
            if (parentheses) {
                return description + space + "(" + prefix + ": " + value + ")";
            } else {
                return description + space + prefix + ": " + value;
            }
        };
    }

    /**
     * Formats a description and value into a string.
     *
     * @param description description or empty string
     * @param value       the default value
     * @return the formatted string
     */
    String format(@NotNull String description, Object value);
}