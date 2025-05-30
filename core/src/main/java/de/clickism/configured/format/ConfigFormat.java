/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for different configuration formats.
 */
public abstract class ConfigFormat {
    /**
     * Whether to separate config options with line breaks.
     */
    protected boolean separateConfigOptions = true;
    /**
     * Whether to write comments (or descriptions).
     */
    protected boolean writeComments = true;

    /**
     * Reads a configuration file and returns the data as a map.
     *
     * @param file the file to read
     * @return the data as a map
     * @throws IOException if an error occurs while reading the file
     */
    public abstract @NotNull Map<String, Object> read(File file) throws Exception;

    /**
     * Writes the data to a configuration file.
     *
     * @param config the config object
     * @param data   the data to write
     * @throws IOException if an error occurs while writing the file
     */
    public abstract void write(Config config, List<Map.Entry<ConfigOption<?>, Object>> data) throws Exception;

    /**
     * Sets whether to separate config options with line breaks.
     *
     * @param separateConfigOptions true to separate config options with line breaks, false otherwise
     */
    public void separateConfigOptions(boolean separateConfigOptions) {
        this.separateConfigOptions = separateConfigOptions;
    }

    /**
     * Sets whether to write comments (or descriptions) in the config file.
     *
     * @param writeComments true to write comments, false otherwise
     */
    public void writeComments(boolean writeComments) {
        this.writeComments = writeComments;
    }
}
