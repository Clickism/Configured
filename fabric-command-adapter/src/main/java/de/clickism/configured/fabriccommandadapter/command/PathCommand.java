/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.fabriccommandadapter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

/**
 * Command to display the path of the configuration file.
 *
 * @param onPath optional message callback executed when the command is run
 */
public record PathCommand(@Nullable OnPath onPath) implements AbstractSubCommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build(Config config) {
        return CommandManager.literal("path")
                .executes(ctx -> {
                    var sender = ctx.getSource();
                    var file = config.file();
                    if (file == null) {
                        return 0;
                    }
                    String path = file.getAbsolutePath();
                    if (onPath != null) {
                        onPath.onPath(sender, path);
                    } else {
                        success(sender, "Config file is located at: §f" + path);
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }

    /**
     * Callback interface for handling the path message.
     */
    public interface OnPath {
        /**
         * Called when the path command is executed.
         *
         * @param sender the command sender
         * @param path   the path of the configuration file
         */
        void onPath(ServerCommandSource sender, String path);
    }
}
