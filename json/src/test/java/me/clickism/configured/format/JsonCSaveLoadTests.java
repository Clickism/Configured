package me.clickism.configured.format;

import me.clickism.configured.Config;
import me.clickism.configured.SaveLoadTests;

public class JsonCSaveLoadTests extends SaveLoadTests {
    @Override
    protected Config createConfig(String path) {
        return Config.of(path, JsonFormat.jsonc());
    }
}
