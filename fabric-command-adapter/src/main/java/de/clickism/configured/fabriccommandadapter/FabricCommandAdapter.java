package de.clickism.configured.fabriccommandadapter;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.configured.Config;
import de.clickism.configured.fabriccommandadapter.command.AbstractSubCommand;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Adapter for creating a command structure for configuration management.
 */
@SuppressWarnings("UnstableApiUsage")
public class FabricCommandAdapter {
    private final Config config;
    private List<AbstractSubCommand> subCommands = new ArrayList<>();
    private @Nullable Predicate<CommandSourceStack> requirement;

    /**
     * Creates a new PaperCommandAdapter.
     *
     * @param config the configuration instance
     */
    protected FabricCommandAdapter(Config config) {
        this.config = config;
    }

    /**
     * Creates a new PaperCommandAdapter from the given configuration.
     *
     * @param config the configuration instance
     * @return a new PaperCommandAdapter
     */
    public static FabricCommandAdapter ofConfig(Config config) {
        return new FabricCommandAdapter(config);
    }

    /**
     * Sets a requirement predicate for the command.
     *
     * @param requirement the requirement predicate
     * @return the current PaperCommandAdapter instance
     */
    public FabricCommandAdapter requires(Predicate<CommandSourceStack> requirement) {
        this.requirement = requirement;
        return this;
    }

    /**
     * Adds a subcommand to the adapter.
     *
     * @param subCommand the subcommand to add
     * @return the current PaperCommandAdapter instance
     */
    public FabricCommandAdapter add(AbstractSubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    /**
     * Builds the root "config" command with all subcommands.
     *
     * @return the literal argument builder for the root command
     */
    public LiteralArgumentBuilder<CommandSourceStack> buildRoot() {
        var root = Commands.literal("config");
        if (requirement != null) {
            root.requires(requirement);
        }
        subCommands.forEach(command -> {
            root.then(command.build(config));
        });
        return root;
    }
}
