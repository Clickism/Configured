/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import java.util.List;

/**
 * Generates configuration keys based on a set of base keys.
 */
public interface KeyGenerator {
    /**
     * KeyGenerator that returns the base keys without any modifications.
     *
     * @return The identity key generator.
     */
    static KeyGenerator identity() {
        return baseKeys -> baseKeys;
    }

    // Key Generators

    /**
     * KeyGenerator that adds a single alternative key mapped from the primary key.
     *
     * @param keyMapper Function to map the primary key to an alternative key.
     * @return A KeyGenerator that adds the mapped alternative key.
     */
    static KeyGenerator withAlternative(SingleKeyMapper keyMapper) {
        return baseKeys -> {
            String mappedKey = keyMapper.map(baseKeys.primary());
            return baseKeys.layer(List.of(mappedKey));
        };
    }

    /**
     * KeyGenerator that adds multiple alternative keys mapped from the primary key.
     *
     * @param keyMapper Function to map the primary key to multiple alternative keys.
     * @return A KeyGenerator that adds the mapped alternative keys.
     */
    static KeyGenerator withAlternatives(MultiKeyMapper keyMapper) {
        return primaryKey -> {
            List<String> mappedKeys = keyMapper.map(primaryKey.primary());
            return primaryKey.layer(mappedKeys);
        };
    }

    /**
     * Generates new keys based on the provided base keys.
     *
     * @param baseKeys The base keys to generate new keys from.
     * @return The generated keys.
     */
    Keys generateKeys(Keys baseKeys);

    // Key Mapper Interfaces

    /**
     * Maps a single primary key to an alternative key.
     */
    @FunctionalInterface
    interface SingleKeyMapper {
        String map(String primaryKey);
    }

    /**
     * Maps a single primary key to multiple alternative keys.
     */
    @FunctionalInterface
    interface MultiKeyMapper {
        List<String> map(String primaryKey);
    }
}
