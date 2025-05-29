package me.clickism.configured.format;

import org.jetbrains.annotations.NotNull;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * YAML format for configuration files.
 */
public class YamlFormat extends BaseFormat {

    private final Load load = new Load(LoadSettings.builder().build());
    private final Dump dump = new Dump(DumpSettings.builder()
            .setMultiLineFlow(true)
            .setDefaultFlowStyle(FlowStyle.BLOCK)
            .build());

    /**
     * Creates a new YamlFormat instance.
     *
     * @return a new instance of YamlFormat
     */
    public static YamlFormat yaml() {
        return new YamlFormat();
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Object> read(File file) throws Exception {
        try {
            Map<String, Object> map = (Map<String, Object>) load.loadFromString(Files.readString(file.toPath()));
            return map != null ? map : new HashMap<>();
        } catch (Exception e) {
            throw new IOException("Failed to read config file: " + file.getPath(), e);
        }
    }

    @Override
    public String formatComment(String comment) {
        return "# " + comment.replaceAll("\n", "\n# ");
    }

    @Override
    protected void writeKeyValue(StringBuilder sb, String key,
                                 Object value, boolean hasNext) throws Exception {
        String string = dump.dumpToString(value);
        sb.append(key).append(":");
        // Handle indentation for collections and maps manually
        if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                sb.append(" []\n");
                return;
            }
            sb.append("\n");
        } else if (value instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                sb.append(" {}\n");
                return;
            }
            sb.append("\n");
            string = "  " + string.replaceAll("\n", "\n  ").stripTrailing();
            sb.append(string);
            sb.append("\n");
            return;
        } else {
            sb.append(" ");
        }
        sb.append(string);
    }

    @Override
    protected void writeFormatHeader(StringBuilder sb) {
        // No header needed for YAML
    }

    @Override
    protected void writeFormatFooter(StringBuilder sb) {
        // No footer needed for YAML
    }
}
