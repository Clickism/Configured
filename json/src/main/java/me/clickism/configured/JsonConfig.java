package me.clickism.configured;

import me.clickism.configured.format.JsonFormat;

import java.io.File;

public final class JsonConfig {
    private JsonConfig() {}

    public static Config ofJson(File file) {
        return new Config(JsonFormat.json(), file);
    }

    public static Config ofJson(String fileName) {
        return ofJson(new File(fileName));
    }

    public static Config ofJsonC(File file) {
        return new Config(JsonFormat.jsonc(), file);
    }

    public static Config ofJsonC(String fileName) {
        return ofJsonC(new File(fileName));
    }
}
