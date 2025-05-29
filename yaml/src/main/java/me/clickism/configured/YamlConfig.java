package me.clickism.configured;

import me.clickism.configured.format.YamlFormat;

import java.io.File;

public final class YamlConfig {
    private YamlConfig() {}

    public static Config of(File file) {
        return new Config(new YamlFormat(), file);
    }

    public static Config of(String fileName) {
        return of(new File(fileName));
    }
}
