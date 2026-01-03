/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.papercommandadapter.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

/**
 * Interface representing a sub-command for a command adapter.
 */
@SuppressWarnings("UnstableApiUsage")
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
    default void success(CommandSender sender, String message) {
        sender.sendRichMessage("<green>[Configured] " + message + "</green>");
    }
}
