package de.clickism.configured.fabriccommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.fabriccommandadapter.argument.ValueArgumentUtil;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

/**
 * Set command to update the value of a configuration option.
 *
 * @param onSet optional message callback to handle the set action.
 */
public record SetCommand(@Nullable OnSet onSet) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(Config config) {
        var set = Commands.literal("set");

        for (ConfigOption<?> option : config.registeredOptions()) {
            if (!ValueArgumentUtil.hasSupportedType(option)) continue;
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
                .then(Commands.argument("value", StringArgumentType.string())
                        .suggests(new ValueArgumentUtil(option)::listSuggestions)
                        .executes(ctx -> execute(config, ctx, option)));
    }

    private <T> int execute(
            Config config,
            CommandContext<CommandSourceStack> ctx,
            ConfigOption<T> option
    ) throws CommandSyntaxException {
        var sender = ctx.getSource();
        Object value = ValueArgumentUtil.parseValue(ctx.getArgument("value", String.class), option);
        option.set(option.cast(value));
        config.save();
        if (onSet != null) {
            onSet.onSet(sender, option.primaryKey(), value);
        } else {
            success(sender, "§\"" + option.primaryKey() + "\"§a set to: §l" + value);
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
                CommandSourceStack sender,
                String optionKey,
                Object value
        );
    }
}
