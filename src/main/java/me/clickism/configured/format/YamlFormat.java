package me.clickism.configured.format;

import me.clickism.configured.ConfigOption;
import me.clickism.configured.Configured;
import org.jetbrains.annotations.NotNull;
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

public class YamlFormat extends ConfigFormat {

    private static final List<@NotNull CommentLine> LINE_BREAK_COMMENT = List.of(
            new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE)
    );

    private final Load load;
    private final DumpSettings dumpSettings;
    private final BaseRepresenter representer;

    public YamlFormat() {
        this.load = new Load(LoadSettings.builder().build());
        this.dumpSettings = DumpSettings.builder()
                .setDumpComments(true)
                .build();
        this.representer = new StandardRepresenter(dumpSettings);
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
    public void write(File file, Map<ConfigOption<?>, Object> data) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            StreamDataWriter streamDataWriter = new YamlOutputStreamWriter(outputStream, StandardCharsets.UTF_8) {
                @Override
                public void processIOException(IOException e) {
                    Configured.LOGGER.log(Level.SEVERE, "Error while writing to config file: " + file.getPath(), e);
                }
            };
            Serializer serializer = new Serializer(dumpSettings, new Emitter(dumpSettings, streamDataWriter));
            serializer.emitStreamStart();
            MappingNode mappingNode = toMappingNode(data);
            serializer.serializeDocument(mappingNode);
            serializer.emitStreamEnd();
        }
    }

    private MappingNode toMappingNode(Map<ConfigOption<?>, Object> data) {
        List<NodeTuple> nodes = new ArrayList<>(data.size());
        Iterator<Map.Entry<ConfigOption<?>, Object>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ConfigOption<?>, Object> entry = iterator.next();
            ConfigOption<?> option = entry.getKey();
            Object value = entry.getValue();

            Node keyNode = representer.represent(option.key());
            // Add description comment
            String description = option.description();
            if (writeComments && description != null) {
                keyNode.setBlockComments(Arrays.stream(description.split("\n"))
                        .map(line -> new CommentLine(Optional.empty(), Optional.empty(), " " + line, CommentType.BLOCK))
                        .toList());
            }
            Node valueNode = representer.represent(value);
            // Add line break comment
            if (separateConfigOptions && iterator.hasNext()) {
                valueNode.setEndComments(LINE_BREAK_COMMENT);
            }
            // Add node to the list
            nodes.add(new NodeTuple(keyNode, valueNode));
        }
        return new MappingNode(Tag.MAP, nodes, FlowStyle.BLOCK);
    }
}
