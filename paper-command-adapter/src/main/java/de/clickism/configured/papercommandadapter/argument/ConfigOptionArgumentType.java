/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.papercommandadapter.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Argument type for selecting a configuration option by its key.
 */
@SuppressWarnings("UnstableApiUsage")
public class ConfigOptionArgumentType implements CustomArgumentType<ConfigOption<?>, String> {
    private final Config config;

    /**
     * Creates a new ConfigOptionArgumentType.
     * @param config the configuration instance
     */
    public ConfigOptionArgumentType(Config config) {
        this.config = config;
    }

    @Override
    public @NotNull ConfigOption<?> parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String key = StringArgumentType.string().parse(reader);
        ConfigOption<?> option = findOptionByKey(key);
        if (option == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(key);
        }
        return option;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext context, @NotNull SuggestionsBuilder builder) {
        config.registeredOptions().forEach(option -> builder.suggest(option.primaryKey()));
        return builder.buildFuture();
    }

    private ConfigOption<?> findOptionByKey(String key) {
        for (ConfigOption<?> option : this.config.registeredOptions()) {
            if (option.primaryKey().equals(key)) {
                return option;
            }
        }
        return null;
    }
}
