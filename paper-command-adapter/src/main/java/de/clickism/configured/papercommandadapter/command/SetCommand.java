package de.clickism.configured.papercommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.papercommandadapter.argument.ValueArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/**
 * Set command to update the value of a configuration option.
 *
 * @param onSet optional message callback to handle the set action.
 */
@SuppressWarnings("UnstableApiUsage")
public record SetCommand(@Nullable OnSet onSet) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(Config config) {
        var set = Commands.literal("set");

        for (ConfigOption<?> option : config.registeredOptions()) {
            if (!ValueArgumentType.hasSupportedType(option)) continue;
            // Hide version option
            if (option.primaryKey().equals(Config.VERSION_KEY)) continue;
            set.then(optionLiteral(config, option));
        }

        return set;
    }

    private LiteralArgumentBuilder<CommandSourceStack> optionLiteral(
            Config config,
            ConfigOption<?> option
    ) {
        return Commands.literal(option.primaryKey())
                .then(Commands.argument("value", new ValueArgumentType(option))
                        .executes(ctx -> execute(config, ctx, option)));
    }

    private <T> int execute(
            Config config,
            CommandContext<CommandSourceStack> ctx,
            ConfigOption<T> option
    ) {
        var sender = ctx.getSource().getSender();
        Object value = ctx.getArgument("value", Object.class);
        option.set(option.cast(value));
        config.save();
        if (onSet != null) {
            onSet.onSet(sender, option.primaryKey(), value);
        } else {
            success(sender, "<bold>" + option.primaryKey() + "</bold> set to: " + value);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Callback interface for handling messages.
     */
    public interface OnSet {
        /**
         * Called when a configuration option is set.
         *
         * @param sender    the command sender
         * @param optionKey the key of the configuration option
         * @param value     the new value
         */
        void onSet(
                CommandSender sender,
                String optionKey,
                Object value
        );
    }
}
