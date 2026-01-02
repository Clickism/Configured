/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Keys record for primary and alternative keys used in {@link ConfigOption}.
 *
 * <p>
 * The primary key is the main identifier for a configuration option,
 * while alternative keys provide backward compatibility or backup options.
 * </p>
 *
 * @param primary      The primary key.
 * @param alternatives The alternative keys.
 */
public record Keys(
        String primary,
        List<String> alternatives
) implements Iterable<String> {
    @Override
    public @NotNull Iterator<String> iterator() {
        return Stream.concat(
                Stream.of(primary),
                alternatives.stream()
        ).iterator();
    }

    /**
     * Creates a new Keys instance with additional alternative keys layered on top of the existing ones.
     *
     * @param alternatives the additional alternative keys to add
     * @return a new Keys instance with the combined alternative keys
     */
    public Keys layer(List<String> alternatives) {
        return new Keys(primary,
                Stream.concat(
                        this.alternatives.stream(),
                        alternatives.stream()
                ).toList());
    }

}
