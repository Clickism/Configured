/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.localization;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.Configured;
import de.clickism.configured.format.ConfigFormat;
import de.clickism.configured.format.ConfigFormatRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

// TODO: Describe more about the localization system

/**
 * Localization class for managing localized messages.
 */
public class Localization {
    private final Function<String, File> fileGenerator;
    private final ConfigFormat format;
    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();

    private String parameterFormat = "{%s}";
    private @Nullable String fallbackLanguage;
    private @Nullable String language;

    private Config config;
    private Config fallbackConfig;

    private @Nullable Integer version;
    private @Nullable ResourceProvider resourceProvider;
    private boolean updateWithNewKeys = false;

    /**
     * Creates a new Localization instance with the specified format and file generator.
     *
     * @param fileGenerator a function that generates a File based on the language code
     * @param format        the format to use for localization files
     */
    public Localization(Function<String, File> fileGenerator, ConfigFormat format) {
        this.format = format;
        this.fileGenerator = fileGenerator;
    }

    /**
     * Creates a new Localization instance with the given path generator function.
     * The format will be determined based on the file extension of the path
     * generated for an empty language code.
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
     * @param pathGenerator a function that generates the path for the localization file based on the language code
     * @return a new Localization instance
     * @throws IllegalArgumentException if no format is found for the file extension
     */
    public static Localization of(Function<String, String> pathGenerator) {
        ConfigFormat format = ConfigFormatRegistry.getFormat(pathGenerator.apply(""));
        return of(pathGenerator, format);
    }

    /**
     * Creates a new Localization instance with the given path generator function
     * and format.
     *
     * @param pathGenerator a function that generates the path for the localization file based on the language code
     * @param format        the format to use for localization files
     * @return a new Localization instance
     */
    public static Localization of(Function<String, String> pathGenerator, ConfigFormat format) {
        return new Localization(pathGenerator.andThen(File::new), format);
    }

    /**
     * Sets the fallback language to use.
     * <p>
     * If the language file for the fallback language
     * does not exist, it will not be generated.
     * <p>
     * The fallback language will be used when the primary language
     * file does not contain a translation for a given key.
     *
     * @param language the language code to use as the fallback language
     * @return this Localization instance
     */
    public Localization fallbackLanguage(String language) {
        this.fallbackLanguage = language;
        return this;
    }

    /**
     * Gets the fallback language code.
     *
     * @return the fallback language code, or null if not set
     */
    public @Nullable String fallbackLanguage() {
        return fallbackLanguage;
    }

    /**
     * Sets the language to use for localization.
     *
     * @param language the language code to use
     * @return this Localization instance
     */
    public Localization language(String language) {
        this.language = language;
        return this;
    }

    /**
     * Gets the language code currently set for localization.
     *
     * @return the language code, or null if not set
     */
    public @Nullable String language() {
        return language;
    }

    /**
     * Sets the version of the localization.
     *
     * <p>See {@link Config#version(int)} for more information.</p>
     *
     * @param version the version number to set
     * @return this Localization instance
     */
    public Localization version(int version) {
        this.version = version;
        return this;
    }

    /**
     * Gets the version of the localization.
     *
     * @return the version number, or null if not set
     */
    public @Nullable Integer version() {
        return version;
    }

    private boolean isVersionMismatch() {
        if (version == null) return false;
        if (config == null) return false; // No config loaded
        return config.currentVersion()
                .map(version -> !version.equals(this.version))
                .orElse(true); // Mismatch if no version set in the config
    }

    /**
     * Registers all keys from the given enum class that implements {@link LocalizationKey}.
     * <p>
     * You only need to register keys if you want to update local files with newer keys
     * when there is a version mismatch.
     * <p>
     * For more information, see {@link Localization#updateWithNewKeys(boolean)}.
     *
     * @param enumClass the enum class containing localization keys
     * @param <T>       the type of the enum, which must implement {@link LocalizationKey}
     * @return this Localization instance
     */
    public <T extends Enum<T> & LocalizationKey> Localization registerKeysFor(Class<T> enumClass) {
        for (Enum<T> enumConstant : enumClass.getEnumConstants()) {
            registerKey((LocalizationKey) enumConstant);
        }
        return this;
    }

    /**
     * Registers a single localization key.
     * <p>
     * You only need to register keys if you want to update local files with newer keys
     * when there is a version mismatch.
     * <p>
     * For more information, see {@link Localization#updateWithNewKeys(boolean)}.
     *
     * @param key the localization key to register
     * @return this Localization instance
     */
    public Localization registerKey(LocalizationKey key) {
        options.add(ConfigOption.ofObject(key.key(), key.key()));
        return this;
    }

    /**
     * Sets whether to update the localization files with new keys.
     * <p>
     * <strong>WARNING:</strong> If you set this to true, make sure to register
     * all localization keys via {@link Localization#registerKeysFor(Class)} for
     * enums or {@link Localization#registerKey(LocalizationKey)} for individual
     * keys. Otherwise, this is DESTRUCTIVE and will remove data unregistered for
     * unregistered keys.
     * <p>
     * This will only affect local files that are not deployed from the resource
     * directory. If the localization files are not local and can be deployed from
     * the resource directory, they will always be updated with new keys regardless
     * of this setting.
     *
     * @param updateWithNewKeys whether to update the localization files with new keys
     * @return this Localization instance
     */
    public Localization updateWithNewKeys(boolean updateWithNewKeys) {
        this.updateWithNewKeys = updateWithNewKeys;
        return this;
    }

    /**
     * Loads the localization files (config) based on the configured language and fallback language.
     * <p>
     * If the config file for the current language or the fallback language
     * does not exist, it will be deployed/generated.
     * <p>
     * This method will try to load the fallback language if no language is set.
     * <p>
     * If no language or fallback language is set, this method will not do
     * anything.
     *
     * @return this Localization instance.
     */
    public Localization load() {
        if (language == null) {
            if (fallbackLanguage == null) {
                Configured.LOGGER.warning("No language or fallback language set for localization!");
                return this;
            }
            Configured.LOGGER.warning("No language code specified for localization, using fallback language...");
            language = fallbackLanguage;
        }
        config = createLanguageConfig(language);
        deployOrGenerateLocalizationFile(config, language);
        if (fallbackLanguage != null && !fallbackLanguage.equals(language)) {
            fallbackConfig = createLanguageConfig(fallbackLanguage);
            deployOrGenerateLocalizationFile(fallbackConfig, fallbackLanguage);
        }
        return this;
    }

    private void deployOrGenerateLocalizationFile(@NotNull Config config, @NotNull String language) {
        if (!config.exists()) {
            if (resourceProvider != null) {
                Configured.LOGGER.info("No localization file found for '" + language + "'. Deploying from resource...");
                deployLocalizationFile(config, language);
                return;
            } else {
                Configured.LOGGER.warning("No localization file found for '" + language + "'. "
                                          + "But no resource directory set! "
                                          + "Generating an empty localization file instead.");
            }
        }
        if (updateWithNewKeys) {
            config.load();
        } else {
            config.loadWithoutUpdating();
        }
        if (isVersionMismatch()) {
            if (resourceProvider != null) {
                Configured.LOGGER.info("Version mismatch detected. Deploying from resource directory for '"
                                       + language + "'...");
                deployLocalizationFile(config, language);
            } else {
                Configured.LOGGER.warning("Version mismatch detected, but no resource directory set! "
                                          + "Please ensure the localization files are up to date");
            }
        }
    }

    private void deployLocalizationFile(@NotNull Config config, @NotNull String language) {
        if (resourceProvider == null) return;
        String path = resourceProvider.pathGenerator.apply(language);
        if (deploySingleResource(resourceProvider.clazz(), path, fileGenerator.apply(language).getPath())) {
            config.loadWithoutUpdating();
            if (version != null && config.currentVersion().isEmpty()) {
                // If the deployed file does not have a version, set it to the current version
                config.version(version);
                // Save the config with the new version and preserve unregistered data
                config.saveWithUnregisteredData();
            }
        } else {
            config.load();
        }
    }

    private Config createLanguageConfig(String language) {
        Config config = new Config(fileGenerator.apply(language), format);
        if (version != null) {
            config.version(version);
        }
        return config.separateConfigOptions(false)
                .registerAll(options);
    }

    /**
     * Retrieves a localized string for the given key with the specified parameters.
     *
     * <p>
     * This method will replace placeholders in the localized string with the
     * provided parameters in the <strong>exact</strong> order they were specified.
     * <p>
     * Example:
     * <blockquote><pre>
     * enum Keys implements LocalizationKey {
     *    {@literal @}Parameters({"user", "attempts"})
     *     WARN_LOGIN_ATTEMPTS;
     * }</pre></blockquote>
     * You can then use this method to get the localized string:
     * <blockquote><pre>
     * localization.get(Keys.WARN_LOGIN_ATTEMPTS, "Clickism", 3);
     * </pre></blockquote>
     * Which will replace the placeholder <code>{user}</code> with <code>Clickism</code>
     * and <code>{attempts}</code> with <code>3</code>.
     *
     * <p>
     * If more parameters are provided than there are placeholders in the localized string, the extra parameters will be ignored.
     * <p>
     * If too few parameters are provided, the remaining placeholders will not be replaced and will remain in the string.
     *
     * @param key    the localization key to retrieve the string for
     * @param params parameters to replace in the localized string, in the order they appear in the key's parameters
     * @return the localized string with parameters replaced, or the localization key if no localization available
     */
    public String get(LocalizationKey key, Object... params) {
        String result = getLocalizedString(key);
        String[] paramNames = key.parameters();
        for (int i = 0; i < Math.min(params.length, paramNames.length); i++) {
            Object param = params[i];
            String paramName = paramNames[i];
            String placeholder = String.format(parameterFormat, paramName);
            result = result.replace(placeholder, String.valueOf(param));
        }
        return result;
    }

    private String getLocalizedString(LocalizationKey key) {
        if (config == null) {
            return getFallbackString(key);
        }
        String localizedString = config.getOrNull(ConfigOption.ofObject(key.key(), null));
        if (localizedString != null) {
            return localizedString;
        }
        return getFallbackString(key);
    }

    private String getFallbackString(LocalizationKey key) {
        if (fallbackConfig != null) {
            return fallbackConfig.get(ConfigOption.ofObject(key.key(), key.key()));
        }
        return key.key();
    }

    /**
     * Sets the resource provider from which localization files can be deployed.
     * <p>
     * If you set a resource provider, the localization file(s) for the current
     * language will be deployed from the resource path generated via the given
     * path generator function. The localization file(s) will be deployed to the
     * file path generated by the file generator function passed to the constructor.
     * See {@link Localization#of(Function)} for more information.
     * <p>
     * The current version of the localization file will be added automatically to
     * the deployed file if the version key is missing in the resource file.
     * <p>
     * The path generator function should take a language code as input and return the
     * path to the localization file for that language.
     * <p>
     * For a path in your resources folder, i.E: <code>main/java/.../resources/</code>,
     * make your path generator starts with <code>/</code> to ensure it starts from the root of the classpath.
     * <p>
     * Example usage: {@code .resourceDirectory(Main.class, lang -> "/locales/" + lang + ".yml");}
     *
     * @param clazz         the class from which the resource directory will be derived,
     *                      usually the main class of your application.
     * @param pathGenerator a function that generates the path to the localization file given a language code.
     * @return this Localization instance
     */
    public Localization resourceProvider(Class<?> clazz, Function<String, String> pathGenerator) {
        this.resourceProvider = new ResourceProvider(clazz, pathGenerator);
        return this;
    }

    /**
     * Retrieves the current parameter format used for placeholders in localized strings.
     *
     * @return the parameter format as a string
     */
    public String parameterFormat() {
        return parameterFormat;
    }

    /**
     * Sets the format for parameter placeholders in localized strings.
     * Only change this if you want to use a different format for parameters in your localization strings.
     * <p>
     * Default is <code>{%s}</code>.
     *
     * @param parameterFormat the format string for parameter placeholders,
     *                        where <code>%s</code> will be replaced by the parameter name.
     * @return this Localization instance
     */
    public Localization parameterFormat(String parameterFormat) {
        this.parameterFormat = parameterFormat;
        return this;
    }

    /**
     * Deploys a single resource from the specified class's resources to a given destination path.
     * The method copies the resource from the classpath to the specified file path on the filesystem.
     * <p>
     * If the resource does not exist or an error occurs during the copy process, the method logs the error
     * and returns false. If the operation is successful, it returns true.
     *
     * @param clazz           the class from whose resources the resource will be retrieved
     * @param resourcePath    the path to the resource within the class's resources
     * @param destinationPath the filesystem path where the resource should be deployed
     * @return true if the resource was successfully deployed, false otherwise
     */
    protected boolean deploySingleResource(Class<?> clazz, String resourcePath, String destinationPath) {
        Configured.LOGGER.info("Deploying resource '" + resourcePath + "' to '" + destinationPath + "'...");
        try (InputStream in = clazz.getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException("Resource not found: " + resourcePath
                                                            + ". Local file will be used instead.");
            File destinationFile = new File(destinationPath);
            File destinationDirectory = destinationFile.getParentFile();
            if (destinationDirectory != null) {
                Files.createDirectories(destinationDirectory.toPath());
            }
            Files.copy(in, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            Configured.LOGGER.severe("Failed to deploy resource: " + e.getMessage());
            return false;
        }
    }

    /**
     * A record representing a resource provider that can generate paths for localization files.
     * It contains the class from which the resource will be retrieved and a function to generate the path.
     */
    private record ResourceProvider(Class<?> clazz, Function<String, String> pathGenerator) {}
}
