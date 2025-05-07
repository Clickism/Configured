package me.clickism.configured.format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
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
         * </p>
         * <ul>
         *     <li>Multi-line strings</li>
         *     <li>Hexadecimal numbers</li>
         * </ul>
         */
        JSON5;

        private boolean allowsComments() {
            return this == JSONC || this == JSON5;
        }
    }

    private final JsonType type;
    private final JsonFactory jsonFactory = new JsonFactory();
    private final ObjectMapper mapper = new ObjectMapper(jsonFactory)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setDefaultPrettyPrinter(new DefaultPrettyPrinter()
                    .withSeparators(Separators.createDefaultInstance()
                            .withObjectFieldValueSpacing(Separators.Spacing.AFTER)));

    /**
     * Creates a new JsonFormat instance with the specified JSON type.
     *
     * @param type the JSON type
     */
    public JsonFormat(JsonType type) {
        this.type = type;
        setupForType(type);
    }

    // TODO: Format specific default value formatting
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

    private static String formatComment(String comment) {
        return "\t// " + comment.replaceAll("\n", "\n\t// ");
    }

    @Override
    public void writeComments(boolean writeComments) {
        super.writeComments(writeComments && type.allowsComments());
    }

    @Override
    public void write(Config config, List<Map.Entry<ConfigOption<?>, Object>> data) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        writeHeader(sb, config.header());
        writeData(sb, data);
        writeFooter(sb, config.footer());
        sb.append('}');
        String string = sb.toString();
        Files.writeString(config.file().toPath(), string);
    }

    private void writeData(StringBuilder sb,
                           List<Map.Entry<ConfigOption<?>, Object>> data) throws Exception {
        Iterator<Map.Entry<ConfigOption<?>, Object>> iterator = data.iterator();
        while (iterator.hasNext()) {
            Map.Entry<ConfigOption<?>, Object> entry = iterator.next();
            ConfigOption<?> option = entry.getKey();
            Object value = entry.getValue();
            writeConfigOption(sb, option, value, iterator.hasNext());
        }
    }

    private void writeConfigOption(StringBuilder sb, ConfigOption<?> option,
                                   Object value, boolean hasNext) throws Exception {
        writeHeader(sb, option.header());
        // Write description
        String description = option.description();
        if (writeComments && description != null) {
            sb.append(formatComment(description)).append('\n');
        }
        String valueString = mapper.writeValueAsString(value);
        valueString = valueString.replaceAll("\n", "\n\t");
        sb.append("\t\"").append(option.key()).append("\": ").append(valueString);
        if (hasNext) {
            sb.append(',');
        }
        sb.append('\n');
        writeFooter(sb, option.footer());
        // Add line break if more options
        if (hasNext && separateConfigOptions) {
            sb.append('\n');
        }
    }

    private void writeHeader(StringBuilder sb, @Nullable String header) {
        if (!writeComments || header == null) return;
        sb.append(formatComment(header)).append("\n\n");
    }

    private void writeFooter(StringBuilder sb, @Nullable String footer) {
        if (!writeComments || footer == null) return;
        sb.append('\n').append(formatComment(footer)).append('\n');
    }

    private void setupForType(JsonType type) {
        switch (type) {
            case JSON -> {
                writeComments(false);
                separateConfigOptions(false); // Make compact by default
            }
            case JSONC -> {
                jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
            }
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
