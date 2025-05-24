package me.clickism.configured.localization;

import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import me.clickism.configured.Configured;
import me.clickism.configured.format.ConfigFormat;
import me.clickism.configured.format.YamlFormat;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Localization class for managing localized messages.
 */
public class Localization {
    private static final String PARAMETER_FORMAT = "{%s}";

    private final Function<String, File> fileGenerator;
    private final ConfigFormat format;
    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();

    private @Nullable String fallbackLanguage;
    private @Nullable String language;

    private Config config;
    private Config fallbackConfig;

    private @Nullable Integer version;

    /**
     * Creates a new Localization instance with the specified format and file generator.
     *
     * @param format        the format to use for localization files
     * @param fileGenerator a function that generates a File based on the language code
     */
    public Localization(ConfigFormat format, Function<String, File> fileGenerator) {
        this.format = format;
        this.fileGenerator = fileGenerator;
    }

    /**
     * Creates a new Localization instance with YAML format and the given path generator.
     *
     * @param pathGenerator a function that generates the path for the localization file based on the language code
     * @return a new Localization instance
     */
    public static Localization ofYaml(Function<String, String> pathGenerator) {
        return new Localization(new YamlFormat(), pathGenerator.andThen(File::new));
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

    public <T extends Enum<T> & LocalizationKey> Localization registerKeysFor(Class<T> enumClass) {
        for (Enum<T> enumConstant : enumClass.getEnumConstants()) {
            registerKey((LocalizationKey) enumConstant);
        }
        return this;
    }

    public Localization registerKey(LocalizationKey key) {
        options.add(ConfigOption.of(key.key(), key.key()));
        return this;
    }

    /**
     * Loads the localization files (config) based on the configured language and fallback language.
     * <p>If the config file for the current language does not exist, it will be generated.</p>
     * <p>If the config file for the fallback language does not exist, it will NOT be generated.</p>
     *
     * <p>This method will not load/generate anything if no language is set.</p>
     *
     * @return this localization instance.
     */
    public Localization load() {
        if (language != null) {
            config = createLanguageConfig(language);
        } else {
            Configured.LOGGER.severe("No language code specified for localization");
        }
        if (fallbackLanguage != null && !fallbackLanguage.equals(language)) {
            fallbackConfig = createLanguageConfig(fallbackLanguage);
        }
        config.load();
        if (fallbackConfig != null) {
            fallbackConfig.load();
        }
        return this;
    }

    private Config createLanguageConfig(String language) {
        Config config = new Config(format, fileGenerator.apply(language));
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
     * provided parameters in the exact order they were specified.
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
            String placeholder = String.format(PARAMETER_FORMAT, paramName);
            result = result.replace(placeholder, String.valueOf(param));
        }
        return result;
    }

    private String getLocalizedString(LocalizationKey key) {
        if (config == null) {
            return getFallbackString(key);
        }
        String localizedString = config.getOrNull(ConfigOption.of(key.key(), null));
        if (localizedString != null) {
            return localizedString;
        }
        return getFallbackString(key);
    }

    private String getFallbackString(LocalizationKey key) {
        if (fallbackConfig != null) {
            return fallbackConfig.get(ConfigOption.of(key.key(), key.key()));
        }
        return key.key();
    }
}
