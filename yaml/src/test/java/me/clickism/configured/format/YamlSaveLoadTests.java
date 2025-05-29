package me.clickism.configured.format;

import me.clickism.configured.Config;
import me.clickism.configured.SaveLoadTests;

public class YamlSaveLoadTests extends SaveLoadTests {
    @Override
    protected Config createConfig(String path) {
        return Config.of(path, YamlFormat.yaml());
    }
}
