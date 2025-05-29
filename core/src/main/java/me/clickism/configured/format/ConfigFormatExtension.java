package me.clickism.configured.format;

import org.jetbrains.annotations.Nullable;

public interface ConfigFormatExtension {
    @Nullable ConfigFormat getFormatForExtension(String extension);
}
