/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.event;

import de.clickism.configured.ConfigOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event bus for configuration events.
 */
public class ConfigEventBus {

    private final Map<ConfigOption<?>, Map<ConfigEventType, List<ConfigEventHandler<?>>>> handlers = new HashMap<>();

    /**
     * Registers an event handler for a specific config option and event type.
     *
     * @param option  the config option
     * @param handler the event handler
     * @param type    the event type
     * @param <T>     the type of the config option
     */
    public <T> void registerHandler(ConfigOption<T> option, ConfigEventHandler<T> handler, ConfigEventType type) {
        handlers
                .computeIfAbsent(option, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new ArrayList<>())
                .add(handler);
    }

    /**
     * Triggers all event handlers for a specific config option and event types.
     *
     * @param option the config option
     * @param value  the value associated with the event
     * @param types  the event types to trigger
     */
    public void trigger(ConfigOption<?> option, Object value, ConfigEventType... types) {
        for (var type : types) {
            triggerSingle(option, value, type);
        }
    }

    /**
     * Triggers event handlers for a specific config option and event type.
     */
    private void triggerSingle(ConfigOption<?> option, Object value, ConfigEventType type) {
        var eventHandlers = handlers
                .getOrDefault(option, Map.of())
                .getOrDefault(type, List.of());
        for (var handler : eventHandlers) {
            handler.handle(value);
        }
    }

    /**
     * Clears event handlers for a specific config option and event type.
     *
     * @param option the config option
     * @param type   the event type
     */
    public void clearHandlers(ConfigOption<?> option, ConfigEventType type) {
        handlers.getOrDefault(option, Map.of()).remove(type);
    }

    /**
     * Clears all event handlers for a specific config option.
     *
     * @param option the config option
     */
    public void clearHandlers(ConfigOption<?> option) {
        handlers.remove(option);
    }

    /**
     * Clears all event handlers for all config options.
     */
    public void clearAllHandlers() {
        handlers.clear();
    }
}
