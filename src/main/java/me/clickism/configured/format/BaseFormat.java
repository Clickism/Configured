package me.clickism.configured.format;

import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Base class to help with the manual writing of config files.
 */
public abstract class BaseFormat extends ConfigFormat {

    /**
     * Formats the given comment string based on the format.
     * Should handle new lines.
     *
     * @param comment the comment string to format
     * @return the formatted comment string
     */
    public abstract String formatComment(String comment);

    /**
     * Writes the key-value pair to the StringBuilder.
     *
     * @param sb      the StringBuilder to write to
     * @param key     the key to write
     * @param value   the value to write
     * @param hasNext true if there is a next entry, false otherwise
     * @throws Exception if an error occurs while writing
     */
    protected abstract void writeKeyValue(StringBuilder sb, String key,
                                          Object value, boolean hasNext) throws Exception;

    /**
     * Write this format's specific file header, if any. i.E. '{' for JSON.
     *
     * @param sb the StringBuilder to write to
     */
    protected abstract void writeFormatHeader(StringBuilder sb);

    /**
     * Write this format's specific file footer, if any. i.E. '}' for JSON.
     *
     * @param sb the StringBuilder to write to
     */
    protected abstract void writeFormatFooter(StringBuilder sb);

    @Override
    public void write(Config config, List<Map.Entry<ConfigOption<?>, Object>> data) throws Exception {
        StringBuilder sb = new StringBuilder();
        writeFormatHeader(sb);
        writeHeader(sb, config.header());
        writeData(sb, data);
        writeFooter(sb, config.footer());
        writeFormatFooter(sb);
        String string = sb.toString();
        Files.writeString(config.file().toPath(), string);
    }

    /**
     * Writes the data/config options to the StringBuilder.
     *
     * @param sb   the StringBuilder to write to
     * @param data the data to write
     * @throws Exception if an error occurs while writing
     */
    protected void writeData(StringBuilder sb, List<Map.Entry<ConfigOption<?>, Object>> data) throws Exception {
        Iterator<Map.Entry<ConfigOption<?>, Object>> iterator = data.iterator();
        while (iterator.hasNext()) {
            Map.Entry<ConfigOption<?>, Object> entry = iterator.next();
            ConfigOption<?> option = entry.getKey();
            Object value = entry.getValue();
            writeConfigOption(sb, option, value, iterator.hasNext());
        }
    }

    /**
     * Writes a config option to the StringBuilder.
     *
     * @param sb      the StringBuilder to write to
     * @param option  the config option to write
     * @param value   the value of the config option
     * @param hasNext true if there is a next entry, false otherwise
     * @throws Exception if an error occurs while writing
     */
    protected void writeConfigOption(StringBuilder sb, ConfigOption<?> option,
                                     Object value, boolean hasNext) throws Exception {
        writeHeader(sb, option.header());
        writeDescription(sb, option.description());
        writeKeyValue(sb, option.key(), value, hasNext);
        writeFooter(sb, option.footer());
        if (hasNext && separateConfigOptions) {
            sb.append('\n');
        }
    }

    /**
     * Writes a description to the StringBuilder.
     *
     * @param sb          the StringBuilder to write to
     * @param description the description to write
     */
    protected void writeDescription(StringBuilder sb, @Nullable String description) {
        if (!writeComments || description == null) return;
        sb.append(formatComment(description)).append('\n');
    }

    /**
     * Writes a header to the StringBuilder.
     *
     * @param sb     the StringBuilder to write to
     * @param header the header to write
     */
    protected void writeHeader(StringBuilder sb, @Nullable String header) {
        if (!writeComments || header == null) return;
        sb.append(formatComment(header)).append("\n\n");
    }

    /**
     * Writes a footer to the StringBuilder.
     *
     * @param sb     the StringBuilder to write to
     * @param footer the footer to write
     */
    protected void writeFooter(StringBuilder sb, @Nullable String footer) {
        if (!writeComments || footer == null) return;
        sb.append('\n').append(formatComment(footer)).append('\n');
    }
}
