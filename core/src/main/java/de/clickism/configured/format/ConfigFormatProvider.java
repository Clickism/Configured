/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;

/**
 * Abstract class for providing configuration formats based on file extensions.
 * This class uses the ServiceLoader mechanism to discover implementations at runtime.
 */
public abstract class ConfigFormatProvider {
    private static final ServiceLoader<ConfigFormatProvider> SERVICE_LOADER = ServiceLoader.load(ConfigFormatProvider.class);

    /**
     * Gets the configuration format for a given file path.
     *
     * @param path the file path, which must include an extension
     * @return the corresponding ConfigFormat for the file extension
     * @throws IllegalArgumentException if the path does not have an extension
     *                                  or if no format is found for the extension
     */
    public static @NotNull ConfigFormat getFormat(String path) throws IllegalArgumentException {
        String extension = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : null;
        if (extension == null) {
            throw new IllegalArgumentException("Path must have an extension: " + path);
        }
        for (ConfigFormatProvider formatExtension : SERVICE_LOADER) {
            ConfigFormat format = formatExtension.getFormatFor(extension);
            if (format != null) {
                return format;
            }
        }
        throw new IllegalArgumentException("No format found for extension: " + extension);
    }

    /**
     * Gets the configuration format for a specific file extension.
     *
     * @param extension the file extension to look up
     * @return the ConfigFormat for the given extension, or null if not found
     */
    public abstract @Nullable ConfigFormat getFormatFor(String extension);
}
