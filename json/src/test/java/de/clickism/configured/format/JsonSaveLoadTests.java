package de.clickism.configured.format;

import de.clickism.configured.Config;
import de.clickism.configured.SaveLoadTests;

public class JsonSaveLoadTests extends SaveLoadTests {
    @Override
    protected Config createConfig(String path) {
        return Config.of(path, JsonFormat.json());
    }
}
