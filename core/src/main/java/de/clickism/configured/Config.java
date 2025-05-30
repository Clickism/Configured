/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import de.clickism.configured.format.ConfigFormat;
import de.clickism.configured.format.ConfigFormatRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Represents a configuration file.
 */
public class Config {
    // TODO: Maybe add a way to add warning to not change the version
    // TODO: Transformation system to update the config file format
    private static final ConfigOption<Integer> VERSION_OPTION = ConfigOption.ofObject("_version", 0);

    private final ConfigFormat format;
    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();

    private @Nullable File file;

    private @Nullable Integer version;
    private Map<String, Object> data = new HashMap<>();

    private @Nullable String header;
    private @Nullable String footer;

    /**
     * Creates a new Config instance.
     *
     * @param file   the file to read/write the config from/to
     * @param format the format of the config file
     */
    public Config(@Nullable File file, ConfigFormat format) {
        this.format = format;
        this.file = file;
    }

    /**
     * Creates a new Config instance with the specified file.
     * The format will be determined based on the file extension.
     * <p>
     * Available formats are:
     * <ul>
     *     <li><code>.yml</code> / <code>.yaml</code>: Standard YAML format</li>
     *     <li><code>.json</code>: Standard JSON format</li>
     *     <li><code>.jsonc</code>: JSON with comments</li>
     * </ul>
     * <p>
     * <strong>WARNING:</strong> Make sure you have the correct format module (i.E: "configured-yaml")
     * added to your project to use the desired format.
     *
     * @param file the file to read/write the config from/to
     * @return a new Config instance
     * @throws IllegalArgumentException if no format is found for the file extension
     */
    public static Config of(@NotNull File file) {
        ConfigFormat format = ConfigFormatRegistry.getFormat(file.getPath());
        return new Config(file, format);
    }

    /**
     * Creates a new Config instance with the specified file path.
     * The format will be determined based on the file extension.
     * <p>
     * Available formats are:
     * <ul>
     *     <li><code>.yml</code> / <code>.yaml</code>: Standard YAML format</li>
     *     <li><code>.json</code>: Standard JSON format</li>
     *     <li><code>.jsonc</code>: JSON with comments</li>
     * </ul>
     * <p>
     * <strong>WARNING:</strong> Make sure you have the correct format module (i.E: "configured-yaml")
     * added to your project to use the desired format.
     *
     * @param filePath the path to the config file
     * @return a new Config instance
     * @throws IllegalArgumentException if no format is found for the file extension
     */
    public static Config of(@NotNull String filePath) {
        return of(new File(filePath));
    }

    /**
     * Creates a new Config instance with the specified file and format.
     *
     * @param file   the file to read/write the config from/to
     * @param format the format of the config file
     * @return a new Config instance
     */
    public static Config of(@Nullable File file, ConfigFormat format) {
        return new Config(file, format);
    }

    /**
     * Creates a new Config instance with the specified file path and format.
     *
     * @param filePath the path to the config file
     * @param format   the format of the config file
     * @return a new Config instance
     */
    public static Config of(@NotNull String filePath, ConfigFormat format) {
        return of(new File(filePath), format);
    }

    /**
     * Creates and registers a new option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * </p>
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @param <T>          the type of the option
     * @return the registered option
     */
    public <T> ConfigOption<T> optionOfObject(String key, T defaultValue) {
        return register(ConfigOption.ofObject(key, defaultValue));
    }

    /**
     * Creates and registers a new option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * </p>
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @return the registered option
     */
    public ConfigOption<Boolean> optionOf(String key, boolean defaultValue) {
        return register(ConfigOption.of(key, defaultValue));
    }

    /**
     * Creates and registers a new option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * </p>
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @param <T>          the type of the option
     * @return the registered option
     */
    public <T extends Number> ConfigOption<T> optionOf(String key, T defaultValue) {
        return register(ConfigOption.of(key, defaultValue));
    }

    /**
     * Creates and registers a new option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * </p>
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @return the registered option
     */
    public ConfigOption<String> optionOf(String key, String defaultValue) {
        return register(ConfigOption.of(key, defaultValue));
    }

    /**
     * Creates and registers a new option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * </p>
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @return the registered option
     */
    public ConfigOption<Character> optionOf(String key, char defaultValue) {
        return register(ConfigOption.of(key, defaultValue));
    }

    /**
     * Creates and registers a new list option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * <p>
     * <strong>WARNING:</strong> Only the elements of the list will be cast to the specified type.
     * Objects won't recursively be cast to the specified type.
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @param elementType  the type of the elements in the list
     * @param <T>          the type of the elements in the list
     * @return the registered option
     */
    public <T> ConfigOption<List<T>> optionOf(String key, List<T> defaultValue,
                                              Class<T> elementType) {
        return register(ConfigOption.of(key, defaultValue, elementType));
    }

    /**
     * Creates and registers a new set option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * <p>
     * <strong>WARNING:</strong> Only the elements of the set will be cast to the specified type.
     * Objects won't recursively be cast to the specified element type.
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @param elementType  the type of the elements in the set
     * @param <T>          the type of the elements in the set
     * @return the registered option
     */
    public <T> ConfigOption<Set<T>> optionOf(String key, Set<T> defaultValue,
                                             Class<T> elementType) {
        return register(ConfigOption.of(key, defaultValue, elementType));
    }

    /**
     * Creates and registers a new map option in the config with the given key and default value.
     * <p>
     * Equivalent to {@code register(ConfigOption.of(key, defaultValue))}.
     * <p>
     * <strong>WARNING:</strong> Only the keys and values of the map will be cast to the specified types.
     * Objects won't recursively be cast to the specified key and value types.
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @param keyType      the type of the keys in the map
     * @param valueType    the type of the values in the map
     * @param <K>          the type of the keys in the map
     * @param <V>          the type of the values in the map
     * @return the registered option
     */
    public <K, V> ConfigOption<Map<K, V>> optionOf(String key, Map<K, V> defaultValue,
                                                   Class<K> keyType, Class<V> valueType) {
        return register(ConfigOption.of(key, defaultValue, keyType, valueType));
    }

    /**
     * Registers a new option in the config.
     *
     * @param option the option to register
     * @param <T>    the type of the option
     * @return the registered option
     */
    public <T> ConfigOption<T> register(ConfigOption<T> option) {
        if (options.contains(option)) {
            throw new IllegalArgumentException("Option with key '" + option.key() + "' already exists");
        }
        options.add(option);
        data.put(option.key(), option.defaultValue());
        return option;
    }

    /**
     * Registers multiple options in the config.
     *
     * @param options the options to register
     * @return this Config instance
     */
    public Config registerAll(Collection<ConfigOption<?>> options) {
        for (ConfigOption<?> option : options) {
            register(option);
        }
        return this;
    }

    /**
     * Gets the value of an option, or the default value if it is not set or has an invalid type.
     *
     * @param option the option to get
     * @param <T>    the type of the option
     * @return the value of the option, or the default value if it is not set
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigOption<T> option) {
        Object value = data.get(option.key());
        if (value == null) {
            return option.defaultValue();
        }
        try {
            return (T) value;
        } catch (ClassCastException ignored) {
            Configured.LOGGER.warning("Invalid value type for option '" + option.key()
                                      + "'. Using default value instead");
            return option.defaultValue();
        }
    }

    /**
     * Gets the value of an option, or null if it is not set or has an invalid type.
     *
     * @param option the option to get
     * @param <T>    the type of the option
     * @return the value of the option, or null if it is not set or has an invalid type
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getOrNull(ConfigOption<T> option) {
        Object value = data.get(option.key());
        if (value == null) {
            return null;
        }
        try {
            return (T) value;
        } catch (ClassCastException ignored) {
            Configured.LOGGER.warning("Invalid value type for option '" + option.key() + "'.");
            return null;
        }
    }

    /**
     * Sets the value of an option.
     *
     * @param option the option to set
     * @param value  the value to set, or null to use the default value
     * @param <T>    the type of the option
     * @return this Config instance
     */
    public <T> Config set(ConfigOption<T> option, @Nullable T value) {
        if (!options.contains(option)) {
            throw new IllegalArgumentException("Option '" + option.key() + "' is not registered");
        }
        if (value == null) {
            data.remove(option.key());
            return this;
        }
        data.put(option.key(), value);
        return this;
    }

    /**
     * Resets the value of an option to its default value.
     *
     * @param option the option to reset
     */
    public void reset(ConfigOption<?> option) {
        set(option, null);
    }

    /**
     * Loads or reloads the config file.
     * <p>This will overwrite any unsaved changes in the config.</p>
     * <p>This WILL create the config file if it does not exist.</p>
     * <p>This WILL update the config file if there is a version mismatch.</p>
     *
     * @return this Config instance
     */
    public Config load() {
        loadInternal(true, true);
        return this;
    }

    /**
     * Loads the config file without creating it or updating it.
     * <p>This will overwrite any unsaved changes in the config.</p>
     * <p>This WILL create the config file if it does not exist.</p>
     * <p>This will NOT update the config file if there is a version mismatch.</p>
     *
     * @return this Config instance
     */
    public Config loadWithoutUpdating() {
        loadInternal(true, false);
        return this;
    }

    /**
     * Loads the config file if it exists, without creating it.
     * <p>This will overwrite any unsaved changes in the config.</p>
     * <p>This will NOT create the config file if it does not exist.</p>
     * <p>This WILL update the config file if there is a version mismatch.</p>
     *
     * @return this Config instance
     */
    public Config loadIfExists() {
        loadInternal(false, true);
        return this;
    }

    /**
     * Loads the config file if it exists, without updating it.
     * <p>This will overwrite any unsaved changes in the config only if the config file exists.</p>
     * <p>This will NOT create the config file if it does not exist.</p>
     * <p>This will NOT update the config file if there is a version mismatch.</p>
     *
     * @return this Config instance
     */
    public Config loadIfExistsWithoutUpdating() {
        loadInternal(false, false);
        return this;
    }

    private void loadInternal(boolean create, boolean update) {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        try {
            if (!file.exists()) {
                // Set the version to the current version
                if (create) {
                    save();
                }
                // Still need to call listeners
                callListeners();
                // Not necessary to load
                return;
            }
            Map<String, Object> data = format.read(file);
            castAllData(data);
            this.data = data;
            if (isVersionMismatch() && update) {
                Configured.LOGGER.info("Config file '" + file.getPath() + "' has a different version. Saving current version.");
                save();
            }
            callListeners();
        } catch (Exception e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to load config file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Cast all data in the config to the correct type.
     */
    private void castAllData(Map<String, Object> data) {
        for (ConfigOption<?> option : options) {
            String key = option.key();
            Object value = data.get(key);
            if (value == null) continue;
            try {
                data.put(key, option.cast(value));
            } catch (ClassCastException e) {
                Configured.LOGGER.warning("Invalid value type for option '" + key
                                          + "'. Using default value instead. Reason: " + e.getMessage());
                data.put(key, option.defaultValue());
            }
        }
    }

    /**
     * Calls the onLoad listeners for all registered options that are set.
     */
    @SuppressWarnings("unchecked")
    private Config callListeners() {
        for (ConfigOption<?> option : options) {
            if (!data.containsKey(option.key())) continue;
            option.onLoadListeners().forEach(listener ->
                    ((Consumer<Object>) listener).accept(get(option)));
        }
        return this;
    }

    /**
     * Saves the config file.
     * This will save all registered options, even if they are not set.
     * If the config file does not exist, it will be created.
     *
     * @return this Config instance
     */
    public Config save() {
        saveInternal(true);
        return this;
    }

    /**
     * Saves all data read in the config file,
     * including data that don't have a corresponding
     * registered option.
     * <p>
     * Will write data in the order:
     * <ol>
     *     <li>Registered options</li>
     *     <li>Unregistered data</li>
     * </ol>
     * <p>
     * The ordering of the unregistered data will only
     * be preserved if the {@link ConfigFormat} returns
     * a {@link LinkedHashMap} or similar after reading
     * the file. Make sure to use a format that
     * preserves the order of the data if the ordering
     * is important.
     *
     * @return this Config instance
     */
    public Config saveWithUnregisteredData() {
        saveInternal(false);
        return this;
    }

    private void saveInternal(boolean onlyRegistered) {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        // Set the version to the current version
        if (options.contains(VERSION_OPTION)) {
            set(VERSION_OPTION, version);
        }
        var dataToSave = getDataToSave(onlyRegistered);
        // Save the data to the file
        try {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                Configured.LOGGER.info("Config file '" + file.getPath() + "' doesn't exist, creating it");
                file.createNewFile();
            }
            format.write(this, dataToSave);
        } catch (Exception e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to save config file: " + file.getAbsolutePath(), e);
        }
    }

    private List<Map.Entry<ConfigOption<?>, Object>> getDataToSave(boolean onlyRegistered) {
        int size = onlyRegistered ? options.size() : data.size();
        List<Map.Entry<ConfigOption<?>, Object>> dataToSave = new ArrayList<>(size);
        // Save all options, even if they are not set
        for (ConfigOption<?> option : options) {
            Object value = data.getOrDefault(option.key(), option.defaultValue());
            if (option.isHidden() && Objects.equals(value, option.defaultValue())) {
                // Don't save hidden options if they are not set
                continue;
            }
            dataToSave.add(Map.entry(option, value));
        }
        if (onlyRegistered) return dataToSave;
        // Save all data, even if they are not registered
        // Will keep order iff the data is a LinkedHashMap or similar.
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            ConfigOption<Object> option = ConfigOption.ofObject(entry.getKey(), entry.getValue());
            if (options.contains(option)) continue; // Skip registered options
            dataToSave.add(Map.entry(option, entry.getValue()));
        }
        return dataToSave;
    }

    /**
     * Gets the file associated with this config.
     *
     * @return the file associated with this config
     */
    public @Nullable File file() {
        return file;
    }

    /**
     * Sets the file associated with this config.
     *
     * @param file the file to set
     * @return this Config instance
     */
    public Config file(File file) {
        this.file = file;
        return this;
    }

    /**
     * Checks if the config file exists.
     *
     * @return true if the config file exists, false otherwise
     */
    public boolean exists() {
        return file != null && file.exists();
    }

    /**
     * Sets the config version of the config file.
     * Used for versioning the config file format.
     *
     * <p>
     * <strong>Warning:</strong> This will register a new option with the key
     * <strong>"_version"</strong> if it doesn't exist or (possibly) overwrite it if it does.
     * </p>
     *
     * @param version the version to set
     * @return this Config instance
     */
    public Config version(int version) {
        this.version = version;
        if (!options.contains(VERSION_OPTION)) {
            register(VERSION_OPTION);
        }
        return this;
    }

    /**
     * Gets the registered version of the config file.+
     * <p>
     * This will return the version that was set using {@link #version(int)}.
     *
     * @return an optional containing the version if it is set, or an empty optional if not
     */
    public Optional<Integer> version() {
        return Optional.ofNullable(version);
    }

    /**
     * Gets the current version of the config file.
     * <p>
     * This will return the version that is currently set in the config file.
     * If the config file does not have a version set, it will return an empty optional.
     *
     * @return an optional containing the current version if it is set, or an empty optional if not
     */
    public Optional<Integer> currentVersion() {
        return Optional.ofNullable(getOrNull(VERSION_OPTION));
    }

    /**
     * Check if the loaded config file has an older version than the current version.
     *
     * @return true if the loaded config file has an older version, false otherwise
     */
    private boolean isVersionMismatch() {
        if (version == null) return false;
        int fileVersion = get(VERSION_OPTION);
        return fileVersion != version;
    }

    /**
     * Sets whether to separate config options with a line break.
     *
     * @param separateConfigOptions true to separate config options with a line break, false otherwise
     * @return this Config instance
     */
    public Config separateConfigOptions(boolean separateConfigOptions) {
        format.separateConfigOptions(separateConfigOptions);
        return this;
    }

    /**
     * Sets whether to write comments (descriptions) in the config file.
     *
     * @param writeComments true to write comments, false otherwise
     * @return this Config instance
     */
    public Config writeComments(boolean writeComments) {
        format.writeComments(writeComments);
        return this;
    }

    /**
     * Gets the header of the config file.
     *
     * @return the header of the config file
     */
    public @Nullable String header() {
        return header;
    }

    /**
     * Sets the header of the config file.
     *
     * @param header the header to set
     * @return this Config instance
     */
    public Config header(String header) {
        this.header = header.trim();
        return this;
    }

    /**
     * Gets the footer of the config file.
     *
     * @return the footer of the config file
     */
    public @Nullable String footer() {
        return footer;
    }

    /**
     * Sets the footer of the config file.
     *
     * @param footer the footer to set
     * @return this Config instance
     */
    public Config footer(String footer) {
        this.footer = footer.trim();
        return this;
    }
}
