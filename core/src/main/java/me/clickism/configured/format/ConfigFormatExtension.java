package me.clickism.configured.format;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for providing configuration formats based on file extensions.
 */
public interface ConfigFormatExtension {
    /**
     * Gets the configuration format for a given file extension.
     *
     * @param extension the file extension (without the dot, e.g., "json", "yaml")
     * @return the corresponding ConfigFormat, or null if no format is found for the extension
     */
    @Nullable ConfigFormat getFormatForExtension(String extension);
}
