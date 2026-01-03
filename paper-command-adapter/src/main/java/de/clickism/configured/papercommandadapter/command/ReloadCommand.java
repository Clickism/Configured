/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.papercommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/**
 * Command to reload the configuration file.
 *
 * @param onReload optional message callback executed when the command is run
 */
@SuppressWarnings("UnstableApiUsage")
public record ReloadCommand(@Nullable OnReload onReload) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(Config config) {
        return Commands.literal("reload")
                .executes(ctx -> {
                    var sender = ctx.getSource().getSender();
                    config.load();
                    if (onReload != null) {
                        onReload.onReload(sender);
                    } else {
                        success(sender, "Config reloaded.");
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }

    /**
     * Callback interface for handling the reload message.
     */
    public interface OnReload {
        /**
         * Called when the reload command is executed.
         *
         * @param sender the command sender
         */
        void onReload(CommandSender sender);
    }
}
