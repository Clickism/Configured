/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import de.clickism.configured.event.ConfigEventHandler;
import de.clickism.configured.event.ConfigEventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a config option.
 * <p>
 * Overrides the {@link #hashCode()} method to return the hash code of the key.
 *
 * @param <T> the type of the config option
 */
public class ConfigOption<T> extends ConfigOptionMeta<ConfigOption<T>> {
    private final String primaryKey;
    private final Set<String> alternativeKeys = new HashSet<>();

    private final T defaultValue;
    private Caster<T> caster;

    private final @Nullable Config config;

    /**
     * Creates a new config option with an inferred caster.
     *
     * @param primaryKey   the key of the config option
     * @param defaultValue the default value of the config option
     * @param config       the config this option belongs to
     */
    public ConfigOption(String primaryKey, @NotNull T defaultValue, @Nullable Config config) {
        this.primaryKey = primaryKey;
        this.defaultValue = defaultValue;
        this.caster = Caster.primitiveOf(defaultValue);
        this.config = config;
    }

    /**
     * Gets the value of the config option.
     *
     * @return the value of the config option
     */
    public T get() {
        if (config == null) {
            Configured.LOGGER.warning("ConfigOption '" + primaryKey + "' does not have a Config instance. Using default value.");
        }
        return config.get(this);
    }

    /**
     * Sets the value of the config option.
     *
     * @param value the value to set
     */
    public void set(T value) {
        if (config == null) {
            Configured.LOGGER.warning("ConfigOption '" + primaryKey + "' does not have a Config instance. Cannot set value.");
            return;
        }
        config.set(this, value);
    }

    /**
     * Resets the value of the config option to its default value.
     */
    public void reset() {
        if (config == null) {
            Configured.LOGGER.warning("ConfigOption '" + primaryKey + "' does not have a Config instance. Cannot reset value.");
            return;
        }
        config.reset(this);
    }

    /**
     * Sets the caster for this config option.
     *
     * @param caster the caster to set
     * @return this config option
     */
    public ConfigOption<T> withCaster(Caster<T> caster) {
        this.caster = caster;
        return this;
    }

    /**
     * Gets the caster for this config option.
     *
     * @return the caster
     */
    public Caster<T> caster() {
        return this.caster;
    }

    /**
     * Sets the caster for this config option to a list of the given element type.
     * <p>
     * See {@link Caster#listOf(Caster)} for more information.
     *
     * @param elementType the element type of the list
     * @return this config option
     */
    @SuppressWarnings("unchecked")
    public ConfigOption<T> listOf(Class<?> elementType) {
        this.caster = (Caster<T>) Caster.listOf(elementType);
        return this;
    }

    /**
     * Sets the caster for this config option to a set of the given element type.
     * <p>
     * See {@link Caster#setOf(Caster)} for more information.
     *
     * @param elementType the element type of the set
     * @return this config option
     */
    @SuppressWarnings("unchecked")
    public ConfigOption<T> setOf(Class<?> elementType) {
        this.caster = (Caster<T>) Caster.setOf(elementType);
        return this;
    }

    /**
     * Sets the caster for this config option to a map of the given key and value types.
     * <p>
     * See {@link Caster#mapOf(Caster, Caster)} for more
     *
     * @param keyType   the key type of the map
     * @param valueType the value type of the map
     * @return this config option
     */
    @SuppressWarnings("unchecked")
    public ConfigOption<T> mapOf(Class<?> keyType, Class<?> valueType) {
        this.caster = (Caster<T>) Caster.mapOf(keyType, valueType);
        return this;
    }

    /**
     * Casts the given object to the type of this config option.
     *
     * @param object the object to cast
     * @return the cast object
     * @throws ClassCastException if the object cannot be cast to the type of this config option
     */
    public T cast(Object object) throws ClassCastException {
        return caster.cast(object);
    }

    /**
     * Gets the key of the config option.
     *
     * @return the key of the config option
     */
    public String primaryKey() {
        return primaryKey;
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
     * Adds an event handler for the specified event type.
     *
     * @param event   the event type
     * @param handler the event handler
     * @return this config option
     */
    public ConfigOption<T> on(ConfigEventType event, ConfigEventHandler<T> handler) {
        if (config == null) {
            Configured.LOGGER.warning("ConfigOption '" + primaryKey + "' does not have a Config instance. Cannot add event handler.");
            return this;
        }
        config.eventBus().registerHandler(this, handler, event);
        return this;
    }

    /**
     * Adds an event handler for when the config option is loaded.
     *
     * @param handler the event handler
     * @return this config option
     */
    public ConfigOption<T> onLoad(ConfigEventHandler<T> handler) {
        return on(ConfigEventType.LOAD, handler);
    }

    /**
     * Adds an event handler for when the config option changes.
     *
     * @param handler the event handler
     * @return this config option
     */
    public ConfigOption<T> onChange(ConfigEventHandler<T> handler) {
        return on(ConfigEventType.CHANGE, handler);
    }

    /**
     * Adds an event handler for when the config option is saved.
     *
     * @param handler the event handler
     * @return this config option
     */
    public ConfigOption<T> onSave(ConfigEventHandler<T> handler) {
        return on(ConfigEventType.SAVE, handler);
    }

    /**
     * Adds an event handler for when the config option is reset.
     *
     * @param handler the event handler
     * @return this config option
     */
    public ConfigOption<T> onReset(ConfigEventHandler<T> handler) {
        return on(ConfigEventType.RESET, handler);
    }

    /**
     * Adds an alternative key for the config option.
     * See {@link Keys} for more information.
     *
     * @param key the alternative key to add
     * @return this config option
     */
    public ConfigOption<T> alternativeKey(String key) {
        this.alternativeKeys.add(key);
        return this;
    }

    /**
     * Adds alternative keys for the config option.
     * See {@link Keys} for more information.
     *
     * @param keys the alternative keys to add
     * @return this config option
     */
    public ConfigOption<T> alternativeKeys(Collection<String> keys) {
        this.alternativeKeys.addAll(keys);
        return this;
    }

    /**
     * Gets the keys of the config option.
     *
     * @return the keys of the config option
     */
    public Keys keys() {
        return new Keys(primaryKey, new ArrayList<>(alternativeKeys));
    }

    // Overrides

    @Override
    public int hashCode() {
        return primaryKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConfigOption<?> other && this.primaryKey.equals(other.primaryKey);
    }
}
