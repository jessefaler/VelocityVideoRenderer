package com.protoxon.display.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.protoxon.display.Display;
import com.protoxon.display.command.subcommand.*;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.format.TextColor;

public class DisplayCommand {

    public static void register() {
        CommandManager commandManager = Display.proxy.getCommandManager();
        // Create the root command
        LiteralArgumentBuilder<CommandSource> root = LiteralArgumentBuilder.literal("display");

        root.executes(DisplayCommand::handleRootCommand); // Handle the execution of /display when no arguments are given

        // Register subcommands
        root.then(CreateCommand.register());  // CREATE
        root.then(SetCommand.register());     // SET
        root.then(PlayCommand.register());    // PLAY
        root.then(StopCommand.register());    // STOP
        root.then(PauseCommand.register());   // PAUSE
        root.then(ResumeCommand.register());  // RESUME
        root.then(TpCommand.register());      // TP
        root.then(DeleteCommand.register());  // DELETE

        // Create the Brigadier command
        BrigadierCommand brigadierCommand = new BrigadierCommand(root);

        // Create command metadata
        CommandMeta commandMeta = commandManager.metaBuilder("sls")
                .plugin(Display.plugin)
                .build();

        // Register the command
        commandManager.register(commandMeta, brigadierCommand);
    }

    private static int handleRootCommand(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        source.sendPlainMessage("invalid command entry");
        return 0;
    }
}
