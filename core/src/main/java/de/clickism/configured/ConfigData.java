/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

/**
 * Class representing configuration data.
 */
public class ConfigData {
    private final Set<ConfigOption<?>> options;

    private final Map<String, Object> data = new HashMap<>();
    private Map<String, Object> rawData = Map.of();

    private KeyGenerator keyGenerator = KeyGenerator.identity();

    /**
     * Creates a new ConfigData instance.
     *
     * @param options the set of registered config options
     */
    public ConfigData(Set<ConfigOption<?>> options) {
        this.options = options;
    }

    /**
     * Gets the value of the specified config option, or its default value if not set.
     *
     * @param option the config option
     * @param <T>    the type of the config option
     * @return the value of the config option, or its default value if not set
     */
    public <T> T getOrDefault(ConfigOption<T> option) {
        Object value = data.get(option.primaryKey());
        if (value == null) {
            return option.defaultValue();
        }
        @SuppressWarnings("unchecked")
        T castedValue = (T) value;
        return castedValue;
    }

    /**
     * Gets the value of the specified config option, or null if not set.
     *
     * @param option the config option
     * @param <T>    the type of the config option
     * @return the value of the config option, or null if not set
     */
    public <T> @Nullable T getOrNull(ConfigOption<T> option) {
        @SuppressWarnings("unchecked")
        T value = (T) data.get(option.primaryKey());
        return value;
    }

    /**
     * Gets the value associated with the specified key, or null if not set.
     *
     * @param key   the key of the config option
     * @param clazz the class of the config option
     * @param <T>   the type of the config option
     * @return the value associated with the key, or null if not set
     */
    public <T> @Nullable T getValue(String key, Class<T> clazz) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        return clazz.cast(value);
    }

    /**
     * Sets the value of the specified config option.
     *
     * @param option the config option
     * @param value  the value to set, or null to remove the option
     * @param <T>    the type of the config option
     */
    public <T> void set(ConfigOption<T> option, @Nullable T value) {
        if (!options.contains(option)) {
            throw new IllegalArgumentException("Option '" + option.primaryKey() + "' is not registered");
        }
        if (value == null) {
            data.remove(option.primaryKey());
            return;
        }
        data.put(option.primaryKey(), value);
    }

    /**
     * Checks if the specified config option is present.
     *
     * @param option the config option
     * @return true if the config option is present, false otherwise
     */
    public boolean contains(ConfigOption<?> option) {
        return data.containsKey(option.primaryKey());
    }

    /**
     * Loads and casts configuration data from a raw map.
     *
     * @param rawMap the raw configuration data map
     */
    public void loadFromMap(Map<String, Object> rawMap) {
        this.rawData = rawMap;
        data.clear();
        data.putAll(rawMap); // Keep unregistered data
        for (var option : options) {
            Object value = resolveValue(option, rawMap);
            data.put(option.primaryKey(), value);
        }
    }

    /**
     * Resolves the value for a given config option from the provided map.
     *
     * @param option the config option
     * @param map    the map containing raw configuration data
     * @param <T>    the type of the config option
     * @return the resolved and casted value
     */
    protected <T> T resolveValue(ConfigOption<T> option, Map<String, Object> map) {
        Keys keys = keyGenerator.generateKeys(option.keys());
        for (var key : keys) {
            if (!map.containsKey(key)) continue;
            // Read and cast value
            boolean isPrimary = key.equals(keys.primary());
            Object rawValue = map.get(key);
            try {
                // Warn if alternative key is used
                if (!isPrimary) {
                    Configured.LOGGER.info("Loading from alternative key \"" + key + "\" for option '" + option.primaryKey() + "'");
                }
                return option.cast(rawValue);
            } catch (ClassCastException e) {
                if (isPrimary) {
                    // Primary key failed, throw exception
                    Configured.LOGGER.log(Level.SEVERE, "Value for option \"" + option + "\" is of invalid type, using default value!", e);
                    break;
                }
                // Ignore alternative key failures
            }
        }
        return option.defaultValue();
    }

    /**
     * Gets the data to be saved based on the specified save policies.
     *
     * @param policies the set of save policies
     * @return a list of map entries representing the data to be saved
     */
    public List<Map.Entry<ConfigOption<?>, Object>> getDataToSave(Set<SavePolicy> policies) {
        List<Map.Entry<ConfigOption<?>, Object>> dataToSave = new ArrayList<>(rawData.size());
        // Save all options, even if they are not set
        for (ConfigOption<?> option : options) {
            Object value = data.getOrDefault(option.primaryKey(), option.defaultValue());
            if (!policies.contains(SavePolicy.SAVE_HIDDEN) && option.hidden() && Objects.equals(value, option.defaultValue())) {
                // Don't save hidden options if they are not set
                continue;
            }
            dataToSave.add(Map.entry(option, value));
        }
        // Save all data, even if they are not registered
        // Will keep order iff the data is a LinkedHashMap or similar.
        if (policies.contains(SavePolicy.INCLUDE_UNREGISTERED)) {
            rawData.forEach((key, value) -> {
                ConfigOption<Object> option = new ConfigOption<>(key, value, null);
                if (options.contains(option)) return; // Skip registered options
                dataToSave.add(Map.entry(option, value));
            });
        }
        return dataToSave;
    }

    /**
     * Sets the key generator for this config data.
     *
     * @param keyGenerator the key generator to set
     */
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
}
