/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.fabriccommandadapter.argument;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.clickism.configured.ConfigOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Argument type for parsing configuration option values based on their expected type.
 */
public class ValueArgumentUtil {
    private final ConfigOption<?> option;

    /**
     * Creates a new ValueArgumentType for the given configuration option.
     *
     * @param option the configuration option
     */
    public ValueArgumentUtil(ConfigOption<?> option) {
        this.option = option;
    }

    /**
     * Suggests possible values based on the configuration option's type.
     *
     * @param context the command context
     * @param builder the suggestions builder
     */
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        Class<?> type = option.defaultValue().getClass();
        if (type == Boolean.class) {
            return BoolArgumentType.bool().listSuggestions(context, builder);
        } else if (type == Integer.class) {
            return IntegerArgumentType.integer().listSuggestions(context, builder);
        } else if (type == Float.class) {
            return FloatArgumentType.floatArg().listSuggestions(context, builder);
        } else if (type == Double.class) {
            return DoubleArgumentType.doubleArg().listSuggestions(context, builder);
        } else {
            return StringArgumentType.string().listSuggestions(context, builder);
        }
    }

    /**
     * Parses the input string into the appropriate type based on the configuration option's default value.
     *
     * @param input  the input string to parse
     * @param option the configuration option
     * @return the parsed value
     * @throws CommandSyntaxException if the input cannot be parsed into the expected type
     */
    public static Object parseValue(String input, ConfigOption<?> option) throws CommandSyntaxException {
        Class<?> type = option.defaultValue().getClass();
        try {
            if (type == Boolean.class) {
                return Boolean.parseBoolean(input);
            } else if (type == Integer.class) {
                return Integer.parseInt(input);
            } else if (type == Float.class) {
                return Float.parseFloat(input);
            } else if (type == Double.class) {
                return Double.parseDouble(input);
            } else {
                // String
                return input;
            }
        } catch (NumberFormatException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(input);
        }
    }

    /**
     * Checks if the given configuration option has a supported type that can be parsed.
     *
     * @param option the configuration option to check
     * @return true if the option has a supported type, false otherwise
     */
    public static boolean hasSupportedType(ConfigOption<?> option) {
        Class<?> type = option.defaultValue().getClass();
        return type == Boolean.class || type == Integer.class
               || type == Float.class || type == Double.class
               || type == String.class;
    }
}
