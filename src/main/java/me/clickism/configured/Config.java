package me.clickism.configured;

import me.clickism.configured.format.ConfigFormat;
import me.clickism.configured.format.YamlFormat;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Represents a configuration file.
 */
public class Config {
    // TODO: Config formats in separate classes
    // TODO: Maybe add a way to add warning to not change the version
    private static final ConfigOption<Integer> VERSION_OPTION = ConfigOption.of("_version", 0);

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
     * @param format the format of the config file
     * @param file   the file to read/write the config from/to
     */
    public Config(ConfigFormat format, @Nullable File file) {
        this.format = format;
        this.file = file;
    }

    /**
     * Creates a new Config instance with the YAML format.
     *
     * @param file the file to read/write the config from/to
     * @return the new Config instance
     */
    public static Config ofYaml(File file) {
        return new Config(new YamlFormat(), file);
    }

    // TODO: Error checking or specific function for when the option is not registered in the config

    /**
     * Creates a new Config instance with the YAML format.
     *
     * @param filePath the path to the file to read/write the config from/to
     * @return the new Config instance
     */
    public static Config ofYaml(String filePath) {
        return ofYaml(new File(filePath));
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
    public <T> ConfigOption<T> optionOf(String key, T defaultValue) {
        return register(ConfigOption.of(key, defaultValue));
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
            Configured.LOGGER.warning("Invalid value type for option '" + option.key() + "'. Using default value instead");
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
     */
    public <T> void set(ConfigOption<T> option, @Nullable T value) {
        if (!options.contains(option)) {
            throw new IllegalArgumentException("Option '" + option.key() + "' is not registered");
        }
        if (value == null) {
            data.remove(option.key());
            return;
        }
        data.put(option.key(), value);
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
     */
    public void load() {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        try {
            if (!file.exists()) {
                // Set the version to the current version
                if (options.contains(VERSION_OPTION)) {
                    set(VERSION_OPTION, version);
                }
                save();
                // Not necessary to load
                return;
            }
            data = format.read(file);
            callListeners();
            if (hasOlderVersion()) {
                Configured.LOGGER.info("Config file '" + file.getPath() + "' has a different version. Saving current version.");
                save();
            }
        } catch (Exception e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to load config file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Loads or reloads the config file only if it exists.
     * <p>This will not create a new config file if it does not exist.</p>
     * <p>This will overwrite any unsaved changes in the config if the config file exists.</p>
     */
    public void loadIfExists() {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        if (!file.exists()) {
            return;
        }
        load();
    }

    /**
     * Calls the onLoad listeners for all registered options that are set.
     */
    @SuppressWarnings("unchecked")
    private void callListeners() {
        for (ConfigOption<?> option : options) {
            if (!data.containsKey(option.key())) continue;
            option.onLoadListeners().forEach(listener ->
                    ((Consumer<Object>) listener).accept(get(option)));
        }
    }

    /**
     * Check if the loaded config file has an older version than the current version.
     *
     * @return true if the loaded config file has an older version, false otherwise
     */
    private boolean hasOlderVersion() {
        if (version == null) return false;
        int fileVersion = get(VERSION_OPTION);
        return fileVersion != version;
    }

    /**
     * Saves the config file.
     * This will save all registered options, even if they are not set.
     * If the config file does not exist, it will be created.
     */
    public void save() {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        // Set the version to the current version
        if (options.contains(VERSION_OPTION)) {
            set(VERSION_OPTION, version);
        }
        // Save all options, even if they are not set
        List<Map.Entry<ConfigOption<?>, Object>> dataToSave = new ArrayList<>(options.size());
        for (ConfigOption<?> option : options) {
            Object value = data.getOrDefault(option.key(), option.defaultValue());
            if (option.isHidden() && Objects.equals(value, option.defaultValue())) {
                // Don't save hidden options if they are not set
                continue;
            }
            dataToSave.add(Map.entry(option, value));
        }
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
