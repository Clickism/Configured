package me.clickism.configured.format;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
@Deprecated
public class JsonFormat extends BaseFormat {

//    private final JsonType type;
//    private final JsonFactory jsonFactory = new JsonFactory();
//    private final ObjectMapper mapper = new ObjectMapper(jsonFactory)
//            .enable(SerializationFeature.INDENT_OUTPUT)
//            .setDefaultPrettyPrinter(new DefaultPrettyPrinter()
//                    .withSeparators(Separators.createDefaultInstance()
//                            .withObjectFieldValueSpacing(Separators.Spacing.AFTER)));

    /**
     * Creates a new JsonFormat instance with the specified JSON type.
     *
     * @param type the JSON type
     */
    public JsonFormat(JsonType type) {
//        this.type = type;
//        setupForType(type);
    }

    // TODO: Format specific default value formatting
    @Override
//    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Object> read(File file) throws IOException {
//        try {
//            return (Map<String, Object>) mapper.readValue(file, Map.class);
//        } catch (Exception e) {
//            throw new IOException("Failed to read config file: " + file.getPath() +
//                                  ". Expected type: " + type.name() +
//                                  ". Please verify the JSON format (.json, .jsonc, .json5).", e);
//        }
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String formatComment(String comment) {
        return "\t// " + comment.replaceAll("\n", "\n\t// ");
    }

    @Override
    protected void writeKeyValue(StringBuilder sb, String key,
                                 Object value, boolean hasNext) throws Exception {
//        String string = mapper.writeValueAsString(value)
//                .replaceAll("\n", "\n\t");
//        sb.append("\t\"").append(key).append("\": ").append(string);
//        if (hasNext) {
//            sb.append(',');
//        }
    }

    @Override
    protected void writeFormatHeader(StringBuilder sb) {
        sb.append("{\n");
    }

    @Override
    protected void writeFormatFooter(StringBuilder sb) {
        sb.append('}');
    }

//    @Override
//    public void writeComments(boolean writeComments) {
//        super.writeComments(writeComments && type.allowsComments());
//    }

//    private void setupForType(JsonType type) {
//        switch (type) {
//            case JSON -> {
//                writeComments(false);
//                separateConfigOptions(false); // Make compact by default
//            }
//            case JSONC -> {
//                jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
//            }
//            case JSON5 -> {
//                jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
//                jsonFactory.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature());
//                jsonFactory.enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature());
//                jsonFactory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
//                jsonFactory.enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature());
//                jsonFactory.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
//                jsonFactory.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature());
//                jsonFactory.enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
//                jsonFactory.enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
//                jsonFactory.enable(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature());
//            }
//        }
//    }

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
}
