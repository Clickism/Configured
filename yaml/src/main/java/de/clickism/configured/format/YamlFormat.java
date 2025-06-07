/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * YAML format for configuration files.
 */
public class YamlFormat extends BaseFormat {

    private final Yaml yaml;

    /**
     * Creates a new YamlFormat instance.
     */
    protected YamlFormat() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
    }

    /**
     * Creates a new YamlFormat instance.
     *
     * @return a new instance of YamlFormat.
     */
    public static YamlFormat yaml() {
        return new YamlFormat();
    }

    @Override
    public @NotNull Map<String, Object> read(File file) throws Exception {
        try {
            Map<String, Object> map = yaml.load(Files.readString(file.toPath()));
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
                                 Object value, boolean hasNext) {
        String string = dumpToString(value);
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

    private String dumpToString(Object value) {
        if (value instanceof Collection<?> collection) {
            // Avoid type casting issues with collections
            return yaml.dump(new ArrayList<>(collection));
        }
        return yaml.dump(value);
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
