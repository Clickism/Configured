package me.clickism.configured.format;

import org.jetbrains.annotations.Nullable;

public class YamlFormatExtension implements ConfigFormatExtension {
    @Override
    public @Nullable ConfigFormat getFormatForExtension(String extension) {
        if (extension.equals("yaml") || extension.equals("yml")) {
            return YamlFormat.yaml();
        } else {
            return null;
        }
    }
}
