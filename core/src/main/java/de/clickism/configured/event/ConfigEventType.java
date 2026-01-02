/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.event;

/**
 * Types of configuration events.
 */
public enum ConfigEventType {
    /**
     * Called after a configuration option is loaded.
     */
    LOAD,
    /**
     * Called after a configuration option is saved.
     */
    SAVE,
    /**
     * Called after a configuration option is changed,
     * including the first time it is loaded.
     * <p>
     * This is the go-to event for reacting to value changes.
     */
    CHANGE,
    /**
     * Called after a configuration option is reset to its default value.
     */
    RESET,
}
