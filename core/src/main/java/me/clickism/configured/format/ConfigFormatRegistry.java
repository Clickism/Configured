package me.clickism.configured.format;

import java.util.ServiceLoader;

public class ConfigFormatRegistry {
    private static final ServiceLoader<ConfigFormatExtension> FORMATS =
            ServiceLoader.load(ConfigFormatExtension.class);

    public static ConfigFormat getFormat(String path) throws IllegalArgumentException {
        String extension = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : null;
        if (extension == null) {
            throw new IllegalArgumentException("Path must have an extension: " + path);
        }
        for (ConfigFormatExtension formatExtension : FORMATS) {
            ConfigFormat format = formatExtension.getFormatForExtension(extension);
            if (format != null) {
                return format;
            }
        }
        throw new IllegalArgumentException("No format found for extension: " + extension);
    }
}
