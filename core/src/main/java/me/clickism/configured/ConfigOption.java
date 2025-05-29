package me.clickism.configured;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a config option.
 * <p>
 * Overrides the {@link #hashCode()} method to return the hash code of the key.
 *
 * @param <T> the type of the config option
 */
public class ConfigOption<T> {
    private final String key;
    private final T defaultValue;
    private final List<Consumer<T>> onLoadListeners = new ArrayList<>();
    private boolean hidden = false;
    private @Nullable String description;
    private @Nullable String header;
    private @Nullable String footer;

    // TODO: Auto-handle different collection types

    /**
     * Creates a new config option.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     */
    protected ConfigOption(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new config option with the given key and default value.
     * <p>
     * <strong>WARNING</strong>: This method will only work if the config format supports
     * objects of the given type. It is not guaranteed to work for all
     * object types. Try to use the specific methods for primitive types
     * and collections instead.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @param <T>          the type of the config option
     * @return the new config option
     */
    public static <T> ConfigOption<T> ofObject(String key, T defaultValue) {
        return new ConfigOption<>(key, defaultValue);
    }

    /**
     * Creates a new config option with the given key and default value.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @return the new config option
     */
    public static ConfigOption<Boolean> of(String key, boolean defaultValue) {
        return ofObject(key, defaultValue);
    }

    /**
     * Creates a new config option with the given key and default value.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @param <T>          the type of the number
     * @return the new config option
     */
    public static <T extends Number> ConfigOption<T> of(String key, T defaultValue) {
        return ofObject(key, defaultValue);
    }

    /**
     * Creates a new config option with the given key and default value.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @return the new config option
     */
    public static ConfigOption<String> of(String key, String defaultValue) {
        return ofObject(key, defaultValue);
    }

    /**
     * Creates a new config option with the given key and default value.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @return the new config option
     */
    public static ConfigOption<Character> of(String key, char defaultValue) {
        return new ConfigOption<>(key, defaultValue) {
            @Override
            public Character cast(Object object) throws ClassCastException {
                if (object instanceof Character character) {
                    return character;
                } else if (object instanceof String string) {
                    if (string.length() != 1) {
                        throw new ClassCastException("String must be a single character: " + string);
                    }
                    return string.charAt(0);
                }
                throw new ClassCastException("Cannot cast into character: " + object.getClass().getName());
            }
        };
    }

    /**
     * Creates a new config option with the given key and default value.
     * <p>
     * <strong>WARNING:</strong> Only the elements of the list will be cast to the specified type.
     * Objects won't recursively be cast to the specified element type.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @param elementType  the type of the elements in the list
     * @param <T>          the type of the elements in the list
     * @return the new config option
     */
    public static <T> ConfigOption<List<T>> of(String key, List<T> defaultValue,
                                               Class<T> elementType) {
        return new ConfigOption<>(key, defaultValue) {
            @Override
            public List<T> cast(Object object) throws ClassCastException {
                if (object instanceof Collection<?> collection) {
                    return collection.stream()
                            .map(element -> cast(element, elementType))
                            .collect(Collectors.toCollection(ArrayList::new));
                }
                throw new ClassCastException("Cannot cast into list: " + object.getClass().getName());
            }
        };
    }

    /**
     * Creates a new config option with the given key and default value.
     * <p>
     * <strong>WARNING:</strong> Only the elements of the set will be cast to the specified type.
     * Objects won't recursively be cast to the specified element type.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @param elementType  the type of the elements in the set
     * @param <T>          the type of the elements in the set
     * @return the new config option
     */
    public static <T> ConfigOption<Set<T>> of(String key, Set<T> defaultValue,
                                              Class<T> elementType) {
        return new ConfigOption<>(key, defaultValue) {
            @Override
            public Set<T> cast(Object object) throws ClassCastException {
                if (object instanceof Collection<?> collection) {
                    return collection.stream()
                            .map(element -> cast(element, elementType))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                }
                throw new ClassCastException("Cannot cast into set: " + object.getClass().getName());
            }
        };
    }

    /**
     * Creates a new config option with the given key and default value.
     * <p>
     * <strong>WARNING:</strong> Only the keys and values of the map will be cast to the specified types.
     * Objects won't recursively be cast to the specified key and value types.
     *
     * @param key          the key of the config option
     * @param defaultValue the default value of the config option
     * @param keyType      the type of the keys in the map
     * @param valueType    the type of the values in the map
     * @param <K>          the type of the keys in the map
     * @param <V>          the type of the values in the map
     * @return the new config option
     */
    public static <K, V> ConfigOption<Map<K, V>> of(String key, Map<K, V> defaultValue,
                                                    Class<K> keyType, Class<V> valueType) {
        return new ConfigOption<>(key, defaultValue) {
            @Override
            public Map<K, V> cast(Object object) throws ClassCastException {
                if (object instanceof Map<?, ?> map) {
                    Map<K, V> castedMap = new LinkedHashMap<>(map.size());
                    map.forEach((key, value) -> {
                        K castedKey = cast(key, keyType);
                        V castedValue = cast(value, valueType);
                        castedMap.put(castedKey, castedValue);
                    });
                    return castedMap;
                }
                throw new ClassCastException("Cannot cast into map: " + object.getClass().getName());
            }
        };
    }

    // TODO: Move to format-specific logic?
    private static String formatDefaultValue(Object defaultValue) {
        if (defaultValue instanceof Collection<?> collection) {
            return "[" +
                   collection.stream()
                           .map(ConfigOption::formatDefaultValue)
                           .collect(Collectors.joining(", "))
                   + "]";
        }
        return String.valueOf(defaultValue);
    }

    /**
     * Casts the given object to the specified type.
     * <p>
     * This method handles primitive types, collections, maps, and numbers.
     *
     * @param object the object to cast
     * @param type   the type to cast to
     * @param <T>    the type of the config option
     * @return the cast object
     * @throws ClassCastException if the object cannot be cast to the specified type
     */
    @SuppressWarnings("unchecked")
    protected static <T> T cast(Object object, Class<T> type) throws ClassCastException {
        if (object == null) return null;
        if (type.isInstance(object)) {
            return type.cast(object);
        }
        if (object instanceof Number number) {
            if (type == Integer.class) {
                return (T) Integer.valueOf(number.intValue());
            }
            if (type == Long.class) {
                return (T) Long.valueOf(number.longValue());
            }
            if (type == Double.class) {
                return (T) Double.valueOf(number.doubleValue());
            }
            if (type == Float.class) {
                return (T) Float.valueOf(number.floatValue());
            }
            if (type == Short.class) {
                return (T) Short.valueOf(number.shortValue());
            }
            if (type == Byte.class) {
                return (T) Byte.valueOf(number.byteValue());
            }
            throw new ClassCastException("Cannot cast into number type: " + type.getName());
        }
        if (type == Character.class) {
            if (object instanceof String string && string.length() == 1) {
                return (T) Character.valueOf(string.charAt(0));
            }
            throw new ClassCastException("Cannot cast into character: " + object.getClass().getName());
        }
        if (object instanceof Collection<?> collection) {
            if (type == List.class) {
                return (T) new ArrayList<>((Collection<?>) collection);
            }
            if (type == Set.class) {
                return (T) new LinkedHashSet<>((Collection<?>) collection);
            }
        }
        if (object instanceof Map<?, ?> map) {
            return (T) new LinkedHashMap<>(map);
        }
        return type.cast(object);
    }

    /**
     * Casts the given object to the type of this config option.
     *
     * @param object the object to cast
     * @return the cast object
     * @throws ClassCastException if the object cannot be cast to the type of this config option
     */
    @SuppressWarnings("unchecked")
    public T cast(Object object) throws ClassCastException {
        return cast(object, (Class<T>) getType());
    }

    /**
     * Gets the type of the config option.
     *
     * @return the type of the config option
     */
    public Class<?> getType() {
        return defaultValue.getClass();
    }

    /**
     * Gets the key of the config option.
     *
     * @return the key of the config option
     */
    public String key() {
        return key;
    }

    /**
     * Gets the default value of the config option.
     *
     * @return the default value of the config option
     */
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Marks the config option as hidden.
     * Hidden config options will be loaded, but their default values will not be
     * written to the config file by default.
     *
     * @return this config option
     */
    public ConfigOption<T> hidden() {
        this.hidden = true;
        return this;
    }

    /**
     * Checks if the config option is hidden.
     * Hidden config options will be loaded, but their default values will not be
     * written to the config file by default.
     *
     * @return true if the config option is hidden, false otherwise
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Adds a listener that will be called when the config option is loaded.
     *
     * @param listener the listener to add
     * @return this config option
     */
    public ConfigOption<T> onLoad(Consumer<T> listener) {
        onLoadListeners.add(listener);
        return this;
    }

    /**
     * Gets the list of listeners that will be called when the config option is loaded.
     *
     * @return the list of listeners
     */
    public List<Consumer<T>> onLoadListeners() {
        return onLoadListeners;
    }

    /**
     * Gets the description of the config option.
     *
     * @return the description of the config option, or null if not set
     */
    public @Nullable String description() {
        return description;
    }

    /**
     * Sets the description of the config option.
     *
     * @param description the description of the config option
     * @return this config option
     */
    public ConfigOption<T> description(String description) {
        this.description = description.trim();
        return this;
    }

    /**
     * Appends the default value to the current description of the config option.
     *
     * @return this config option
     */
    public ConfigOption<T> appendDefaultValue() {
        if (description != null) {
            description += "\n";
        }
        appendDefaultValueInternal();
        return this;
    }

    /**
     * Appends the default value to the current description of the config option inlined,
     * without a new line.
     *
     * @return this config option
     */
    public ConfigOption<T> appendInlinedDefaultValue() {
        if (description != null) {
            description += " ";
        }
        appendDefaultValueInternal();
        return this;
    }

    /**
     * Appends the default value to the current description of the config option in parentheses
     * and inlined, without a new line.
     *
     * @return this config option
     */
    public ConfigOption<T> appendParenthesizedDefaultValue() {
        if (description != null) {
            description += " ";
        }
        description += "(";
        appendDefaultValueInternal();
        description += ")";
        return this;
    }

    private void appendDefaultValueInternal() {
        String string = description == null ? "" : description;
        description = string + "Default: " + formatDefaultValue(defaultValue);
    }

    /**
     * Gets the header of the config option.
     *
     * @return the header of the config option, or null if not set
     */
    public @Nullable String header() {
        return header;
    }

    /**
     * Sets the header of the config option.
     *
     * @param header the header of the config option
     * @return this config option
     */
    public ConfigOption<T> header(String header) {
        this.header = header.trim();
        return this;
    }

    /**
     * Gets the footer of the config option.
     *
     * @return the footer of the config option, or null if not set
     */
    public @Nullable String footer() {
        return footer;
    }

    /**
     * Sets the footer of the config option.
     *
     * @param footer the footer of the config option
     * @return this config option
     */
    public ConfigOption<T> footer(String footer) {
        this.footer = footer.trim();
        return this;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConfigOption<?> other && this.key.equals(other.key);
    }
}
