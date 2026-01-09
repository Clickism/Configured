/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.neoforgecommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.neoforgecommandadapter.argument.ConfigOptionArgumentUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.Nullable;

/**
 * Get command to retrieve the value of a configuration option.
 *
 * @param onGet optional message callback to handle the get action.
 */
public record GetCommand(@Nullable OnGet onGet) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(Config config) {
        var optionArg = new ConfigOptionArgumentUtil(config);
        return Commands.literal("get")
                .then(Commands.argument("option", StringArgumentType.string())
                        .suggests(optionArg::listSuggestions)
                        .executes(ctx -> {
                            var sender = ctx.getSource();
                            ConfigOption<?> option = optionArg.findOptionByKey(ctx.getArgument("option", String.class));
                            var value = option.get();
                            if (onGet != null) {
                                onGet.onGet(sender, option.primaryKey(), value);
                            } else {
                                success(sender, "§l\"" + option.primaryKey() + "\"§a has value: §l" + value);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    /**
     * Callback interface for handling messages.
     */
    public interface OnGet {
        /**
         * Called when a configuration option is retrieved.
         *
         * @param sender    the command sender
         * @param optionKey the key of the configuration option
         * @param value     the retrieved value
         */
        void onGet(
                CommandSourceStack sender,
                String optionKey,
                Object value
        );
    }
}
