package me.clickism.configured;

import me.clickism.configured.format.ConfigFormat;
import me.clickism.configured.format.YamlFormat;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class Config {
    private final ConfigFormat format;
    private final File file;

    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();
    private Map<String, Object> data = new HashMap<>();

    public Config(ConfigFormat format, File file) {
        this.format = format;
        this.file = file;
    }

    public <T> ConfigOption<T> register(ConfigOption<T> option) {
        if (options.contains(option)) {
            throw new IllegalArgumentException("Option with key '" + option.key() + "' already exists");
        }
        options.add(option);
        data.put(option.key(), option.defaultValue());
        return option;
    }

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

    public <T> void set(ConfigOption<T> option, T value) {
        if (!options.contains(option)) {
            throw new IllegalArgumentException("Option '" + option.key() + "' is not registered");
        }
        data.put(option.key(), value);
    }

    public void load() {
        try {
            data = format.read(file);
        } catch (IOException e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to load config file: " + file.getAbsolutePath(), e);
        }
    }

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

    public Config version(int version) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Config separateConfigOptions(boolean separateConfigOptions) {
        format.separateConfigOptions(separateConfigOptions);
        return this;
    }

    public Config writeComments(boolean writeComments) {
        format.writeComments(writeComments);
        return this;
    }

    public static Config ofYaml(File file) {
        return new Config(new YamlFormat(), file);
    }

    public static Config ofYaml(String filePath) {
        return new Config(new YamlFormat(), new File(filePath));
    }

//    static Config ofJson5(String filePath) {
//        return new Json5Config();
//    }
}
