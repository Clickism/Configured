package de.clickism.configured.format;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for configuration formats based on file extensions.
 */
public class ConfigFormatRegistry {
    private static final Map<String, ConfigFormat> FORMATS = new HashMap<>();

    static {
        loadFormatAndApply("me.clickism.configured.format.JsonFormat", clazz -> {
            FORMATS.put("json", (ConfigFormat) clazz.getMethod("json").invoke(null));
            FORMATS.put("jsonc", (ConfigFormat) clazz.getMethod("jsonc").invoke(null));
        });
        loadFormatAndApply("me.clickism.configured.format.YamlFormat", clazz -> {
            FORMATS.put("yaml", (ConfigFormat) clazz.getMethod("yaml").invoke(null));
            FORMATS.put("yml", (ConfigFormat) clazz.getMethod("yaml").invoke(null));
        });
    }

    private static void loadFormatAndApply(String className, ClassConsumer consumer) {
        try {
            Class<?> clazz = Class.forName(className);
            consumer.accept(clazz);
        } catch (Exception ignored) {
        }
    }

    /**
     * Registers a configuration format with a specific file extension.
     * @param extension the file extension (without the dot, e.g., "json", "yaml")
     * @param format the ConfigFormat to register
     */
    public static void registerFormat(String extension, ConfigFormat format) {
        FORMATS.put(extension.toLowerCase(), format);
    }

    /**
     * Gets the configuration format for a given file path.
     *
     * @param path the file path, which must include an extension
     * @return the corresponding ConfigFormat for the file extension
     * @throws IllegalArgumentException if the path does not have an extension
     *                                  or if no format is found for the extension
     */
    public static ConfigFormat getFormat(String path) throws IllegalArgumentException {
        String extension = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : null;
        if (extension == null) {
            throw new IllegalArgumentException("Path must have an extension: " + path);
        }
        ConfigFormat format = FORMATS.get(extension);
        if (format == null) {
            throw new IllegalArgumentException("No format found for extension: " + extension);
        }
        return format;
    }

    /**
     * Utility interface for consuming classes and handling exceptions.
     */
    private interface ClassConsumer {
        void accept(Class<?> clazz) throws Exception;
    }
}
