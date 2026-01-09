/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.neoforgecommandadapter.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Interface representing a sub-command for a command adapter.
 */
public interface AbstractSubCommand {
    /**
     * Builds the command's argument structure.
     *
     * @param config the configuration instance
     * @return the literal argument builder for the command
     */
    LiteralArgumentBuilder<CommandSourceStack> build(Config config);

    /**
     * Sends a standardized success message to the sender.
     *
     * @param sender  the command sender
     * @param message the success message
     */
    default void success(CommandSourceStack sender, String message) {
        sender.sendSuccess(() -> Component.literal("§a[Configured] " + message), false);
    }
}
