package me.clickism.configured.format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON format for configuration files.
 *
 * <p>
 * This class supports different JSON flavors, including JSON, JSONC (JSON with comments),
 * and JSON5 (albeit with minor limitations).
 * </p>
 *
 * <p>
 * JSONC: This format allows comments in the JSON file, but is otherwise standard JSON.
 * </p>
 * <p>
 * JSON5: This format allows for more relaxed syntax rules, such as unquoted keys and
 * </p>
 */
public class JsonFormat extends ConfigFormat {

    /**
     * Enum representing the supported JSON flavors.
     */
    public enum JsonType {
        /**
         * Standard JSON format.
         */
        JSON,

        /**
         * This format allows comments in the JSON file, but is otherwise standard JSON.
         */
        JSONC,

        /**
         * This format allows for more relaxed syntax rules, such as trailing commas and
         * unquoted keys.
         * <p>
         * The implementation is based on the Jackson library and supports
         * most of the JSON5 standard except for:
         * <ul>
         *     <li>Multi-line strings</li>
         *     <li>Hexadecimal numbers</li>
         * </ul>
         * </p>
         */
        JSON5
    }

    private final JsonType type;
    private final JsonFactory jsonFactory = new JsonFactory();
    private final ObjectMapper mapper = new ObjectMapper(jsonFactory)
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Creates a new JsonFormat instance with the specified JSON type.
     *
     * @param type the JSON type
     */
    public JsonFormat(JsonType type) {
        this.type = type;
        enableFeaturesFor(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(File file) throws IOException {
        try {
            return (Map<String, Object>) mapper.readValue(file, Map.class);
        } catch (Exception e) {
            throw new IOException("Failed to read config file: " + file.getPath() +
                                  ". Expected type: " + type.name() +
                                  ". Please verify the JSON format (.json, .jsonc, .json5).", e);
        }
    }

    @Override
    public void write(Config config, List<Map.Entry<ConfigOption<?>, Object>> data) throws IOException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        data.forEach(entry -> {
            map.put(entry.getKey().key(), entry.getValue());
        });

        String string = mapper.writeValueAsString(map);
        Files.writeString(config.file().toPath(), string);
    }

    private void enableFeaturesFor(JsonType type) {
        switch (type) {
            case JSONC -> jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
            case JSON5 -> {
                jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
                jsonFactory.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature());
                jsonFactory.enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature());
                jsonFactory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
                jsonFactory.enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature());
                jsonFactory.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
                jsonFactory.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature());
                jsonFactory.enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
                jsonFactory.enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
                jsonFactory.enable(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature());
            }
        }
    }
}
