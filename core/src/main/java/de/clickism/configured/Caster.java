/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Caster functional interface for type casting.
 *
 * @param <T> the target type
 */
@FunctionalInterface
@SuppressWarnings("unchecked")
public interface Caster<T> {
    // Casters

    /**
     * Unchecked caster that performs a raw cast.
     *
     * @param <T> the target type
     * @return the unchecked caster
     */
    static <T> Caster<T> unchecked() {
        return obj -> (T) obj;
    }

    /**
     * Creates a caster for the specified class.
     * <p>
     * Works for both primitive and object types.
     *
     * @param clazz the target class
     * @param <T>   the target type
     * @return the caster
     */
    static <T> Caster<T> of(@NotNull Class<T> clazz) {
        if (isPrimitiveClass(clazz)) {
            return primitiveOf(clazz);
        }
        return obj -> {
            if (clazz.isInstance(obj)) {
                return (T) obj;
            }
            throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + clazz);
        };
    }

    /**
     * Creates a primitive caster based on an example value.
     *
     * @param example the example value
     * @param <T>     the target type
     * @return the caster
     */
    static <T> Caster<T> primitiveOf(T example) {
        return primitiveOf((Class<T>) example.getClass());
    }

    /**
     * Creates a primitive caster for the specified class.
     *
     * @param clazz the target class
     * @param <T>   the target type
     * @return the caster
     */
    static <T> Caster<T> primitiveOf(@NotNull Class<T> clazz) {
        if (Number.class.isAssignableFrom(clazz)) {
            return (Caster<T>) numberOf((Class<? extends Number>) clazz);
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return obj -> {
                if (obj instanceof Boolean bool) {
                    return (T) bool;
                }
                throw new ClassCastException("Cannot cast " + obj.getClass() + " to Boolean");
            };
        }
        if (clazz == String.class) {
            return obj -> {
                if (obj instanceof String str) {
                    return (T) str;
                }
                throw new ClassCastException("Cannot cast " + obj.getClass() + " to String");
            };
        }
        if (clazz == Character.class || clazz == char.class) {
            return obj -> {
                if (obj instanceof Character c) return (T) c;
                if (obj instanceof String s && s.length() == 1) return (T) Character.valueOf(s.charAt(0));
                throw new ClassCastException("Cannot cast " + obj.getClass() + " to Character");
            };
        }
        // Fallback to unchecked cast
        return unchecked();
    }

    /**
     * Checks if the specified class is a primitive or a wrapper type.
     *
     * @param clazz the class to check
     * @return true if the class is a primitive or a wrapper type, false otherwise
     */
    static boolean isPrimitiveClass(@NotNull Class<?> clazz) {
        return clazz.isPrimitive() ||
               clazz == Boolean.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Float.class ||
               clazz == Double.class ||
               clazz == Character.class ||
               clazz == String.class;
    }

    /**
     * Creates a number caster for the specified number class.
     *
     * @param clazz the target number class
     * @param <T>   the target number type
     * @return the caster
     */
    private static <T extends Number> Caster<T> numberOf(@NotNull Class<T> clazz) {
        return obj -> {
            if (obj instanceof Number number) {
                if (clazz == Integer.class) {
                    return (T) Integer.valueOf(number.intValue());
                } else if (clazz == Long.class) {
                    return (T) Long.valueOf(number.longValue());
                } else if (clazz == Double.class) {
                    return (T) Double.valueOf(number.doubleValue());
                } else if (clazz == Float.class) {
                    return (T) Float.valueOf(number.floatValue());
                } else if (clazz == Short.class) {
                    return (T) Short.valueOf(number.shortValue());
                } else if (clazz == Byte.class) {
                    return (T) Byte.valueOf(number.byteValue());
                }
            }
            throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + clazz);
        };
    }

    // Collection Casters

    /**
     * Creates a caster for a list of elements using the specified element caster.
     *
     * @param elementCaster the caster for the list elements
     * @param <T>           the element type
     * @return the list caster
     */
    static <T> Caster<List<T>> listOf(@NotNull Caster<T> elementCaster) {
        return obj -> {
            if (obj instanceof Collection<?> collection) {
                return collection.stream()
                        .map(elementCaster::cast)
                        .collect(Collectors.toCollection(ArrayList::new));
            }
            throw new ClassCastException("Cannot cast " + obj.getClass() + " to List");
        };
    }

    /**
     * Creates a caster for a list of elements using the specified element class.
     *
     * @param elementClass the class of the list elements
     * @param <T>          the element type
     * @return the list caster
     */
    static <T> Caster<List<T>> listOf(@NotNull Class<T> elementClass) {
        return listOf(of(elementClass));
    }

    /**
     * Creates a caster for a set of elements using the specified element caster.
     *
     * @param elementCaster the caster for the set elements
     * @param <T>           the element type
     * @return the set caster
     */
    static <T> Caster<Set<T>> setOf(@NotNull Caster<T> elementCaster) {
        return obj -> {
            if (obj instanceof Collection<?> collection) {
                return collection.stream()
                        .map(elementCaster::cast)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }
            throw new ClassCastException("Cannot cast " + obj.getClass() + " to Set");
        };
    }

    /**
     * Creates a caster for a set of elements using the specified element class.
     *
     * @param elementClass the class of the set elements
     * @param <T>          the element type
     * @return the set caster
     */
    static <T> Caster<Set<T>> setOf(@NotNull Class<T> elementClass) {
        return setOf(of(elementClass));
    }

    /**
     * Creates a caster for a map using the specified key and value casters.
     *
     * @param keyCaster   the caster for the map keys
     * @param valueCaster the caster for the map values
     * @param <K>         the key type
     * @param <V>         the value type
     * @return the map caster
     */
    static <K, V> Caster<Map<K, V>> mapOf(@NotNull Caster<K> keyCaster, @NotNull Caster<V> valueCaster) {
        return obj -> {
            if (obj instanceof Map<?, ?> map) {
                Map<K, V> result = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    K key = keyCaster.cast(entry.getKey());
                    V value = valueCaster.cast(entry.getValue());
                    result.put(key, value);
                }
                return result;
            }
            throw new ClassCastException("Cannot cast " + obj.getClass() + " to Map");
        };
    }

    /**
     * Creates a caster for a map using the specified key and value classes.
     *
     * @param keyClass   the class of the map keys
     * @param valueClass the class of the map values
     * @param <K>        the key type
     * @param <V>        the value type
     * @return the map caster
     */
    static <K, V> Caster<Map<K, V>> mapOf(@NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
        return mapOf(of(keyClass), of(valueClass));
    }

    /**
     * Casts an object to the target type.
     *
     * @param obj the object to cast
     * @return the casted object
     * @throws ClassCastException if the object cannot be casted
     */
    T cast(Object obj) throws ClassCastException;

}
