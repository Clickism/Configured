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

public class Localization {
    private static final String PARAMETER_FORMAT = "{%s}";

    private final Function<String, File> fileGenerator;
    private final ConfigFormat format;
    private final Set<ConfigOption<?>> options = new LinkedHashSet<>();

    private @Nullable String fallbackLanguage;
    private @Nullable String language;

    private Config config;
    private Config fallbackConfig;

    public Localization(ConfigFormat format, Function<String, File> fileGenerator) {
        this.format = format;
        this.fileGenerator = fileGenerator;
    }

    public static Localization ofYaml(Function<String, String> pathGenerator) {
        return new Localization(new YamlFormat(), pathGenerator.andThen(File::new));
    }

    public Localization fallbackLanguage(String languageCode) {
        this.fallbackLanguage = languageCode;
        return this;
    }

    public @Nullable String fallbackLanguage() {
        return fallbackLanguage;
    }

    public Localization language(String languageCode) {
        this.language = languageCode;
        return this;
    }

    public @Nullable String language() {
        return language;
    }

    public <T extends Enum<T> & LocalizationKey> Localization registerOptionsFor(Class<T> enumClass) {
        for (Enum<T> enumConstant : enumClass.getEnumConstants()) {
            LocalizationKey key = (LocalizationKey) enumConstant;
            options.add(ConfigOption.of(key.key(), key.key()));
        }
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
            File file = fileGenerator.apply(language);
            config = new Config(format, file);
            config.registerAll(options);
            config.load();
        } else {
            Configured.LOGGER.severe("No language code specified for localization");
        }
        if (fallbackLanguage != null && !fallbackLanguage.equals(language)) {
            File fallbackFile = fileGenerator.apply(fallbackLanguage);
            fallbackConfig = new Config(format, fallbackFile);
            fallbackConfig.loadIfExists();
        }
        return this;
    }

    public String get(LocalizationKey key, Object... params) {
        String result = getLocalizedString(key);
        String[] paramNames = ParameterRegistry.getParameters(key);
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
