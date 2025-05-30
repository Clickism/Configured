package de.clickism.configured.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
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
public class JsonFormat extends BaseFormat {

    private final JsonType type;
    private final Gson gson;

    /**
     * Creates a new JsonFormat instance with the specified JSON type.
     *
     * @param type the JSON type
     */
    public JsonFormat(JsonType type) {
        this.type = type;
        GsonBuilder builder = new GsonBuilder();
        setupForType(builder);
        this.gson = builder.setPrettyPrinting()
                .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
                .create();
    }

    /**
     * Creates a new JsonFormat instance for standard JSON.
     *
     * @return a new instance of JsonFormat for standard JSON
     */
    public static JsonFormat json() {
        return new JsonFormat(JsonType.JSON);
    }

    /**
     * Creates a new JsonFormat instance for JSONC (JSON with comments).
     *
     * @return a new instance of JsonFormat for JSONC
     */
    public static JsonFormat jsonc() {
        return new JsonFormat(JsonType.JSONC);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Object> read(File file) throws IOException {
        try {
            Map<String, Object> map = gson.fromJson(new FileReader(file), Map.class);
            if (map == null) throw new IOException("GSON returned null!");
            return map;
        } catch (Exception e) {
            throw new IOException("Failed to read config file: " + file.getPath() +
                                  ". Expected type: " + type.name() +
                                  ". Please verify the JSON format (.json, .jsonc, .json5).", e);
        }
    }

    @Override
    public String formatComment(String comment) {
        return "  // " + comment.replaceAll("\n", "\n  // ");
    }

    @Override
    protected void writeKeyValue(StringBuilder sb, String key,
                                 Object value, boolean hasNext) {
        String string = gson.toJson(value)
                .replaceAll("\n", "\n  ");
        sb.append("  \"").append(key).append("\": ").append(string);
        if (hasNext) {
            sb.append(',');
        }
        sb.append('\n');
    }

    @Override
    protected void writeFormatHeader(StringBuilder sb) {
        sb.append("{\n");
    }

    @Override
    protected void writeFormatFooter(StringBuilder sb) {
        sb.append('}');
    }

    @Override
    public void writeComments(boolean writeComments) {
        super.writeComments(writeComments && type.allowsComments());
    }

    private void setupForType(GsonBuilder builder) {
        if (type == JsonType.JSON) {
            writeComments(false);
            separateConfigOptions(false); // Make compact by default
            builder.setLenient();
        }
    }

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
        JSONC;

        private boolean allowsComments() {
            return this == JSONC;
        }
    }
}