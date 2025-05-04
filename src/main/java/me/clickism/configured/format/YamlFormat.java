package me.clickism.configured.format;

import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import me.clickism.configured.Configured;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.representer.BaseRepresenter;
import org.snakeyaml.engine.v2.representer.StandardRepresenter;
import org.snakeyaml.engine.v2.serializer.Serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

/**
 * YAML format for configuration files.
 */
public class YamlFormat extends ConfigFormat {

    private static final CommentLine LINE_BREAK_COMMENT =
            new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE);

    private final Load load;
    private final DumpSettings dumpSettings;
    private final BaseRepresenter representer;

    /**
     * Creates a new YamlFormat instance.
     */
    public YamlFormat() {
        this.load = new Load(LoadSettings.builder().build());
        this.dumpSettings = DumpSettings.builder()
                .setDumpComments(true)
                .build();
        this.representer = new StandardRepresenter(dumpSettings);
    }

    private static String formatComment(String comment) {
        return "# " + comment.replaceAll("\n", "\n# ");
    }

    private static List<CommentLine> formatCommentLines(String comment) {
        if (comment == null) return List.of();
        return Arrays.stream(comment.split("\n"))
                .map(line -> new CommentLine(Optional.empty(), Optional.empty(),
                        " " + line, CommentType.BLOCK))
                .toList();
    }

    private static StreamDataWriter createStreamDataWriter(FileOutputStream outputStream, File file) {
        return new YamlOutputStreamWriter(outputStream, StandardCharsets.UTF_8) {
            @Override
            public void processIOException(IOException e) {
                Configured.LOGGER.log(Level.SEVERE, "Error while writing to config file: " + file.getPath(), e);
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(File file) throws IOException {
        String yaml = Files.readString(file.toPath());
        Object result = load.loadFromString(yaml);
        if (result instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        } else {
            throw new IOException("Yaml file does not contain a map");
        }
    }

    @Override
    public void write(Config config, List<Map.Entry<ConfigOption<?>, Object>> data) throws IOException {
        File file = config.file();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            StreamDataWriter streamDataWriter = createStreamDataWriter(outputStream, file);
            Serializer serializer = new Serializer(dumpSettings, new Emitter(dumpSettings, streamDataWriter));
            serializer.emitStreamStart();
            // Write header comment
            String header = config.header();
            if (writeComments && header != null) {
                streamDataWriter.write(formatComment(header) + "\n\n");
            }
            // Write data
            MappingNode mappingNode = toMappingNode(data);
            serializer.serializeDocument(mappingNode);
            // Write footer comment
            String footer = config.footer();
            if (writeComments && footer != null) {
                streamDataWriter.write("\n\n" + formatComment(footer));
            }
            serializer.emitStreamEnd();
        }
    }

    private MappingNode toMappingNode(List<Map.Entry<ConfigOption<?>, Object>> data) {
        List<NodeTuple> nodes = new ArrayList<>(data.size());
        Iterator<Map.Entry<ConfigOption<?>, Object>> iterator = data.iterator();
        while (iterator.hasNext()) {
            Map.Entry<ConfigOption<?>, Object> entry = iterator.next();
            ConfigOption<?> option = entry.getKey();
            Object value = entry.getValue();

            Node keyNode = representer.represent(option.key());
            Node valueNode = representer.represent(value);
            // Add comments
            keyNode.setBlockComments(getBlockComments(option));
            boolean addLineBreak = separateConfigOptions && iterator.hasNext();
            valueNode.setEndComments(getEndComments(option, addLineBreak));

            nodes.add(new NodeTuple(keyNode, valueNode));
        }
        return new MappingNode(Tag.MAP, nodes, FlowStyle.BLOCK);
    }

    private List<CommentLine> getBlockComments(ConfigOption<?> option) {
        if (!writeComments) return List.of();
        List<CommentLine> comments = new ArrayList<>();
        String header = option.header();
        String description = option.description();
        if (header != null) {
            comments.addAll(formatCommentLines(header));
            comments.add(LINE_BREAK_COMMENT);
        }
        if (description != null) {
            comments.addAll(formatCommentLines(description));
        }
        return comments;
    }

    private List<CommentLine> getEndComments(ConfigOption<?> option, boolean addLineBreak) {
        List<CommentLine> comments = new ArrayList<>();
        String footer = option.footer();
        if (writeComments && footer != null) {
            comments.add(LINE_BREAK_COMMENT);
            comments.addAll(formatCommentLines(footer));
        }
        if (addLineBreak) {
            comments.add(LINE_BREAK_COMMENT);
        }
        return comments;
    }
}
