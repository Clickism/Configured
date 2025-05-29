package me.clickism.configured.format;

import java.util.ServiceLoader;

/**
 * Registry for configuration formats based on file extensions.
 * Uses ServiceLoader to discover available {@link ConfigFormatExtension} implementations.
 */
public class ConfigFormatRegistry {
    private static final ServiceLoader<ConfigFormatExtension> FORMATS =
            ServiceLoader.load(ConfigFormatExtension.class);

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
        for (ConfigFormatExtension formatExtension : FORMATS) {
            ConfigFormat format = formatExtension.getFormatForExtension(extension);
            if (format != null) {
                return format;
            }
        }
        throw new IllegalArgumentException("No format found for extension: " + extension);
    }
}
