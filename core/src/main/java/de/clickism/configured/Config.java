/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import de.clickism.configured.comments.HeaderFooter;
import de.clickism.configured.event.ConfigEventBus;
import de.clickism.configured.event.ConfigEventType;
import de.clickism.configured.format.ConfigFormat;
import de.clickism.configured.format.ConfigFormatProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

// TODO: Maybe add a way to add warning to not change the version
// TODO: Transformation system to update the config file format

/**
 * Represents a configuration file.
 */
public class Config extends HeaderFooter<Config> {
    private final ConfigFormat format;

    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();
    private final ConfigData configData = new ConfigData(options);

    private final ConfigEventBus eventBus = new ConfigEventBus();

    private final ConfigOption<Integer> versionOption =
            new ConfigOption<>("_version", 1, this);
    private @Nullable File file;
    private @Nullable Integer version = null;

    /**
     * Creates a new Config instance.
     *
     * @param file   the file to read/write the config from/to
     * @param format the format of the config file
     */
    protected Config(@Nullable File file, ConfigFormat format) {
        this.format = format;
        this.file = file;
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
        ConfigFormat format = ConfigFormatProvider.getFormat(filePath,
                ConfigFormatProvider.getCallerClass());
        return of(filePath, format);
    }

    /**
     * Creates a new Config instance with the specified file path and format.
     *
     * @param filePath the path to the config file
     * @param format   the format of the config file
     * @return a new Config instance
     */
    public static Config of(@NotNull String filePath, ConfigFormat format) {
        return new Config(new File(filePath), format);
    }

    /**
     * Creates and registers a new option in the config with the given key and default value.
     *
     * @param key          the key of the option
     * @param defaultValue the default value of the option
     * @return the created ConfigOption
     */
    public <T> ConfigOption<T> option(String key, T defaultValue) {
        ConfigOption<T> option = new ConfigOption<>(key, defaultValue, this);
        register(option);
        return option;
    }

    /**
     * Gets the value of an option, or the default value if it doesn't have a valid value.
     *
     * @param option the option to get
     * @param <T>    the type of the option
     * @return the value of the option, or the default value if it is not set
     */
    public <T> T get(ConfigOption<T> option) {
        return configData.getOrDefault(option);
    }

    /**
     * Gets the value of an option, or null if it doesn't have a valid value.
     *
     * @param option the option to get
     * @param <T>    the type of the option
     * @return the value of the option, or null if it is not set or has an invalid type
     */
    public <T> @Nullable T getOrNull(ConfigOption<T> option) {
        return configData.getOrNull(option);
    }

    /**
     * Gets the value of an option by its key, or null if it doesn't have a valid value.
     * <p>
     * Prefer using {@link #get(ConfigOption)} to ensure type safety.
     *
     * @param key  the key of the option
     * @param type the type of the option
     * @param <T>  the type of the option
     * @return the value of the option, or null if it is not set or has an invalid type
     */
    public <T> T getValue(String key, Class<T> type) {
        return configData.getOrNull(new ConfigOption<>(key, null, Caster.of(type), null));
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
        configData.set(option, value);
        eventBus.trigger(option, value, ConfigEventType.CHANGE);
        return this;
    }

    /**
     * Resets the value of an option to its default value.
     *
     * @param option the option to reset
     */
    public void reset(ConfigOption<?> option) {
        configData.set(option, null);
        eventBus.trigger(option, option.defaultValue(), ConfigEventType.RESET);
    }

    /**
     * Registers a new option in the config.
     */
    public <T> void register(ConfigOption<T> option) {
        if (options.contains(option)) {
            throw new IllegalArgumentException("Option with key \"" + option.primaryKey() + "\" already exists");
        }
        options.add(option);
        configData.set(option, option.defaultValue());
    }

    /**
     * Registers multiple options in the config.
     */
    public Config registerAll(Collection<ConfigOption<?>> options) {
        for (ConfigOption<?> option : options) {
            register(option);
        }
        return this;
    }

    /**
     * Loads the config from the file.
     * <p>
     * By default, it will create the file if it doesn't exist
     * and update it if the version doesn't match.
     *
     * @param policies the load policies to apply
     * @return this Config instance
     */
    public Config load(LoadPolicy... policies) {
        Set<LoadPolicy> policySet = new HashSet<>(Arrays.asList(policies));
        loadInternal(policySet);
        return this;
    }

    private void loadInternal(Set<LoadPolicy> policies) {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        try {
            if (!file.exists()) {
                // Set the version to the current version
                if (!policies.contains(LoadPolicy.IGNORE_MISSING_FILE)) {
                    save();
                }
                // Still need to call listeners
                options.forEach(option -> eventBus.trigger(option, option.get(), ConfigEventType.LOAD, ConfigEventType.CHANGE));
                // Not necessary to load
                return;
            }
            // Read and cast data
            Map<String, Object> rawData = format.read(file);
            configData.loadFromMap(rawData);
            // Check version
            if (isVersionMismatch() && !policies.contains(LoadPolicy.IGNORE_VERSION_MISMATCH)) {
                Configured.LOGGER.info("Config file \"" + file.getPath() + "\" has a different version. Saving current version.");
                save();
            }
            // Call listeners
            options.forEach(option -> eventBus.trigger(option, option.get(), ConfigEventType.LOAD, ConfigEventType.CHANGE));
        } catch (Exception e) {
            Configured.LOGGER.log(Level.SEVERE, "Failed to load config file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Saves the config to the file.
     *
     * @param policies the save policies to apply
     * @return this Config instance
     */
    public Config save(SavePolicy... policies) {
        Set<SavePolicy> policySet = new HashSet<>(Arrays.asList(policies));
        saveInternal(policySet);
        return this;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveInternal(Set<SavePolicy> policies) {
        if (file == null) {
            Configured.LOGGER.severe("No file specified for config!");
            return;
        }
        // Set the version to the current version
        version().ifPresent(version -> {
            if (options.contains(versionOption)) {
                set(versionOption, version);
            }
        });
        var dataToSave = configData.getDataToSave(policies);
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
            // Trigger save events
            dataToSave.forEach(entry -> {
                var option = (ConfigOption<?>) entry.getKey();
                var value = entry.getValue();
                eventBus.trigger(option, value, ConfigEventType.SAVE);
            });
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
        if (!options.contains(versionOption)) {
            register(versionOption);
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
     * Gets the current/saved version of the config file.
     * <p>
     * This will return the version that is currently saved in the config file.
     * If the config file does not have a version set, it will return an empty optional.
     *
     * @return an optional containing the current version if it is set, or an empty optional if not
     */
    public Optional<Integer> savedVersion() {
        return Optional.ofNullable(getOrNull(versionOption));
    }

    /**
     * Check if the loaded config file has an older version than the current version.
     *
     * @return true if the loaded config file has an older version, false otherwise
     */
    private boolean isVersionMismatch() {
        return version()
                .flatMap(version -> savedVersion()
                        .map(savedVersion -> !savedVersion.equals(version)))
                .orElse(false);
    }

    /**
     * Sets the key generator for this config.
     *
     * @param keyGenerator the key generator to set
     * @return this Config instance
     */
    public Config keyGenerator(KeyGenerator keyGenerator) {
        this.configData.setKeyGenerator(keyGenerator);
        return this;
    }

    /**
     * Configures the format of the config file.
     *
     * @param configurator the configurator to use
     * @return this Config instance
     */
    public Config configureFormat(Consumer<ConfigFormat> configurator) {
        configurator.accept(format);
        return this;
    }

    /**
     * Gets the event bus for this config.
     *
     * @return the event bus
     */
    public ConfigEventBus eventBus() {
        return eventBus;
    }
}
