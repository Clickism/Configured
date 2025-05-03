package me.clickism.configured;

import me.clickism.configured.format.ConfigFormat;
import me.clickism.configured.format.YamlFormat;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents a configuration file.
 */
public class Config {
    private final ConfigFormat format;
    private final File file;

    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();
    private Map<String, Object> data = new HashMap<>();

    /**
     * Creates a new Config instance.
     *
     * @param format the format of the config file
     * @param file   the file to read/write the config from/to
     */
    public Config(ConfigFormat format, File file) {
        this.format = format;
        this.file = file;
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

    // TODO: Error checking or specific function for when the option is not registered in the config

    /**
     * Gets the value of an option, or the default value if it is not set.
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
     * Sets the value of an option.
     *
     * @param option the option to set
     * @param value  the value to set
     * @param <T>    the type of the option
     */
    public <T> void set(ConfigOption<T> option, T value) {
        if (!options.contains(option)) {
            throw new IllegalArgumentException("Option '" + option.key() + "' is not registered");
        }
        data.put(option.key(), value);
    }

    /**
     * Loads or reloads the config file.
     * This will overwrite any unsaved changes in the config.
     */
    public void load() {
        try {
            data = format.read(file);
        } catch (IOException e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to load config file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Saves the config file.
     * This will save all registered options, even if they are not set.
     * If the config file does not exist, it will be created.
     */
    public void save() {
        // Save all options, even if they are not set
        List<Map.Entry<ConfigOption<?>, Object>> dataToSave = new ArrayList<>(options.size());
        for (ConfigOption<?> option : options) {
            Object value = data.getOrDefault(option.key(), option.defaultValue());
            dataToSave.add(Map.entry(option, value));
        }
        // Save the data to the file
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                Configured.LOGGER.info("Config file '" + file.getPath() + "' doesn't exist, creating it");
                file.createNewFile();
            }
            format.write(file, dataToSave);
        } catch (IOException e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to save config file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Sets the config version of the config file.
     * Used for versioning the config file format.
     *
     * @param version the version to set
     * @return this Config instance
     */
    public Config version(int version) {
        throw new UnsupportedOperationException("Not implemented yet");
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
     * Creates a new Config instance with the YAML format.
     *
     * @param file the file to read/write the config from/to
     * @return the new Config instance
     */
    public static Config ofYaml(File file) {
        return new Config(new YamlFormat(), file);
    }

    /**
     * Creates a new Config instance with the YAML format.
     *
     * @param filePath the path to the file to read/write the config from/to
     * @return the new Config instance
     */
    public static Config ofYaml(String filePath) {
        return new Config(new YamlFormat(), new File(filePath));
    }
}
