package com.protoxon.display.command.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.protoxon.display.Display;
import com.protoxon.display.DisplayInstance;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Files;
import java.nio.file.Path;

public class PlayCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("play")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 1;
                })
                .then(type());
    }

    private static RequiredArgumentBuilder<CommandSource, String> type() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("type", StringArgumentType.string())
                .suggests((context, builder) -> {
                    builder.suggest("file");
                    builder.suggest("url");
                    return builder.buildFuture();
                })
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 0;
                })
                .then(path());
    }

    private static RequiredArgumentBuilder<CommandSource, String> path() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("path", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    String type = StringArgumentType.getString(context, "type");
                    if(type.equals("file")) {
                        builder.suggest("/home/jesse/Downloads/southpark.mkv");
                        builder.suggest("/home/jesse/Downloads/fight.mp4");
                        builder.suggest("/home/jesse/Downloads/moves.mp4");
                    } else if (type.equals("url")) {
                        builder.suggest("https://youtu.be/Frtax3pXPtg?feature=shared");
                    }
                    return builder.buildFuture();
                })
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    String type = StringArgumentType.getString(context, "type");
                    String path = StringArgumentType.getString(context, "path");
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                    if(displayInstance == null) {
                        source.sendMessage(Component.text("You do not have a display", NamedTextColor.RED));
                        return 1;
                    }
                    switch (type) {
                        case "file":
                            if(!doseVideoExist(path, source)) return 1;
                            displayInstance.playFromFile(path);
                            source.sendMessage(Component.text("Playing video from file " + path, NamedTextColor.GRAY));
                            return 1;
                        case "url":
                            displayInstance.playFromURL(path);
                            source.sendMessage(Component.text("Streaming video from url " + path, NamedTextColor.GRAY));
                            return 1;
                            default:
                            source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    }
                    return 0;
                });
    }

    public static boolean doseVideoExist(String videoFile, CommandSource source) {
        if(Files.exists(Path.of(videoFile))) {
            return true;
        }
        source.sendMessage(Component.text("Video file not found: " + videoFile, NamedTextColor.RED));
        return false;
    }

}
