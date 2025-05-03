package me.clickism.configured.format;

import me.clickism.configured.ConfigOption;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class ConfigFormat {
    protected boolean separateConfigOptions = true;
    protected boolean writeComments = true;

    public abstract Map<String, Object> read(File file) throws IOException;
    public abstract void write(File file, List<Map.Entry<ConfigOption<?>, Object>> data) throws IOException;

    public void separateConfigOptions(boolean separateConfigOptions) {
        this.separateConfigOptions = separateConfigOptions;
    }

    public void writeComments(boolean writeComments) {
        this.writeComments = writeComments;
    }
}
