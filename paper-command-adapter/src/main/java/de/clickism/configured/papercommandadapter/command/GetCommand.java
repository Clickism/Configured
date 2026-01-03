package de.clickism.configured.papercommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.papercommandadapter.argument.ConfigOptionArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/**
 * Get command to retrieve the value of a configuration option.
 *
 * @param onGet optional message callback to handle the get action.
 */
@SuppressWarnings("UnstableApiUsage")
public record GetCommand(@Nullable OnGet onGet) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(Config config) {
        return Commands.literal("get")
                .then(Commands.argument("option", new ConfigOptionArgumentType(config))
                        .executes(ctx -> {
                            var sender = ctx.getSource().getSender();
                            ConfigOption<?> option = ctx.getArgument("option", ConfigOption.class);
                            var value = option.get();
                            if (onGet != null) {
                                onGet.onGet(sender, option.primaryKey(), value);
                            } else {
                                success(sender, "<bold>" + option.primaryKey() + "</bold> has value: " + value);
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
                CommandSender sender,
                String optionKey,
                Object value
        );
    }
}
