/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

/**
 * Policies for saving configuration files.
 */
public enum SavePolicy {
    /**
     * Whether to save unregistered config options.
     * Prevents data loss when config options are removed from the code.
     */
    INCLUDE_UNREGISTERED,
    /**
     * Whether to save hidden config options.
     */
    SAVE_HIDDEN,
}
