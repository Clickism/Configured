package de.clickism.configured.format;

import de.clickism.configured.Config;
import de.clickism.configured.SaveLoadTests;

public class YamlSaveLoadTests extends SaveLoadTests {
    @Override
    protected Config createConfig(String path) {
        return Config.of(path, YamlFormat.yaml());
    }
}
