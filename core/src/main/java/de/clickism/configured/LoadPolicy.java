/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

/**
 * Policies for loading configuration files.
 */
public enum LoadPolicy {
    /**
     * Ignore missing config file.
     * If the config file is missing, it won't be created automatically.
     */
    IGNORE_MISSING_FILE,
    /**
     * Ignores version mismatches between the config file and the current version.
     * If the versions don't match, the config file will be loaded as-is.
     */
    IGNORE_VERSION_MISMATCH,
}
