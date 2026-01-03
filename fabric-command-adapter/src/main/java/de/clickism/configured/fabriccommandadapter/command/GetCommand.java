package de.clickism.configured.fabriccommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.fabriccommandadapter.argument.ConfigOptionArgumentUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

/**
 * Get command to retrieve the value of a configuration option.
 *
 * @param onGet optional message callback to handle the get action.
 */
public record GetCommand(@Nullable OnGet onGet) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build(Config config) {
        var optionArg = new ConfigOptionArgumentUtil(config);
        return CommandManager.literal("get")
                .then(CommandManager.argument("option", StringArgumentType.string())
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
                ServerCommandSource sender,
                String optionKey,
                Object value
        );
    }
}
