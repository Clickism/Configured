/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.neoforgecommandadapter.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Argument type for selecting a configuration option by its key.
 */
public class ConfigOptionArgumentUtil {
    private final Config config;

    /**
     * Creates a new ConfigOptionArgumentType.
     *
     * @param config the configuration instance
     */
    public ConfigOptionArgumentUtil(Config config) {
        this.config = config;
    }

    /**
     * Suggests possible configuration option keys.
     *
     * @param context the command context
     * @param builder the suggestions builder
     * @return a future with the suggestions
     */
    public @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<?> context, @NotNull SuggestionsBuilder builder) {
        config.registeredOptions().forEach(option -> builder.suggest(option.primaryKey()));
        return builder.buildFuture();
    }

    /**
     * Finds a configuration option by its key.
     *
     * @param key the key of the configuration option
     * @return the configuration option, or null if not found
     */
    public ConfigOption<?> findOptionByKey(String key) {
        for (ConfigOption<?> option : this.config.registeredOptions()) {
            if (option.primaryKey().equals(key)) {
                return option;
            }
        }
        return null;
    }
}
