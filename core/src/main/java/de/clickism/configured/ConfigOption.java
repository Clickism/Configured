/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

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

    private final @Nullable T defaultValue;
    // TODO: Add on change listeners?
    private final List<Consumer<T>> onLoadListeners = new ArrayList<>();
    private final List<Consumer<T>> onChangeListeners = new ArrayList<>();
    private final @Nullable Config config;
    private Caster<T> caster;

    /**
     * Creates a new config option with an inferred caster.
     *
     * @param primaryKey   the key of the config option
     * @param defaultValue the default value of the config option
     * @param config       the config this option belongs to
     */
    public ConfigOption(String primaryKey, @NotNull T defaultValue, @Nullable Config config) {
        this(primaryKey, defaultValue, Caster.primitiveOf(defaultValue), config);
    }

    /**
     * Creates a new config option with a custom caster.
     *
     * @param primaryKey   the key of the config option
     * @param defaultValue the default value of the config option
     * @param caster       the caster for this config option
     * @param config       the config this option belongs to
     */
    public ConfigOption(String primaryKey, @Nullable T defaultValue, Caster<T> caster, @Nullable Config config) {
        this.primaryKey = primaryKey;
        this.defaultValue = defaultValue;
        this.caster = caster;
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
     * Calls all on-load listeners with the current value.
     */
    public void callOnLoadListeners() {
        for (var listener : onLoadListeners) {
            listener.accept(get());
        }
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

    @Override
    public int hashCode() {
        return primaryKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConfigOption<?> other && this.primaryKey.equals(other.primaryKey);
    }
}
