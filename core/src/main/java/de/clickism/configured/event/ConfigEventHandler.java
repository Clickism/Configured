/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.event;

/**
 * Interface for handling configuration events.
 *
 * @param <T> the type of the configuration value
 */
@FunctionalInterface
public interface ConfigEventHandler<T> {
    /**
     * Handles a configuration event with the given value.
     *
     * @param value the configuration value associated with the event
     */
    void handle(Object value);
}
