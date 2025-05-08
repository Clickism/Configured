package me.clickism.configured.format;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * YAML format for configuration files.
 */
public class YamlFormat extends BaseFormat {
    YAMLFactory yamlFactory = new YAMLFactory();
    YAMLMapper mapper = new YAMLMapper(yamlFactory)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS);

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(File file) throws Exception {
        try {
            return (Map<String, Object>) mapper.readValue(file, Map.class);
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
        String string = mapper.writeValueAsString(Map.of(key, value));
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
