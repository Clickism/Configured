package me.clickism.configured.format;

import me.clickism.configured.Config;
import me.clickism.configured.SaveLoadTests;

public class JsonSaveLoadTests extends SaveLoadTests {
    @Override
    protected Config createConfig(String path) {
        return Config.of(path, JsonFormat.json());
    }
}
