package me.clickism.configured.localization;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParameterRegistry {
    private static final Set<String> REGISTERED_CLASSES = new HashSet<>();
    private static final Map<LocalizationKey, String[]> PARAMETERS = new HashMap<>();

    public static String[] getParameters(LocalizationKey key) {
        registerParametersForParentClass(key);
        return PARAMETERS.getOrDefault(key, new String[0]);
    }

    private static void registerParametersForParentClass(LocalizationKey key) {
        if (key == null) return;
        Class<?> clazz = key.getClass();
        String className = clazz.getName();
        if (REGISTERED_CLASSES.contains(className)) return;
        REGISTERED_CLASSES.add(className);
        for (Field field : clazz.getDeclaredFields()) {
            registerParametersForField(field);
        }
    }

    private static void registerParametersForField(Field field) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(null);
        } catch (Exception e) {
            return;
        }
        if (field.isAnnotationPresent(Parameters.class) && value instanceof LocalizationKey valueKey) {
            Parameters params = field.getAnnotation(Parameters.class);
            PARAMETERS.put(valueKey, params.value());
        }
    }
}
