package me.clickism.configured.format;

import org.jetbrains.annotations.Nullable;

public class JsonFormatExtension implements ConfigFormatExtension {
    @Override
    public @Nullable ConfigFormat getFormatForExtension(String extension) {
        if (extension.equals("json")) {
            return JsonFormat.json();
        } else if (extension.equals("jsonc")) {
            return JsonFormat.jsonc();
        } else {
            return null;
        }
    }
}
