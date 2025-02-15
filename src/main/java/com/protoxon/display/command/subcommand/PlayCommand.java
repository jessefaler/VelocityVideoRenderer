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
                    builder.suggest("mp4");
                    builder.suggest("mkv");
                    builder.suggest("youtube");
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
                    builder.suggest("/home/jesse/Downloads/southpark.mkv");
                    builder.suggest("/home/jesse/Downloads/fight.mp4");
                    builder.suggest("/home/jesse/Downloads/moves.mp4");
                    return builder.buildFuture();
                })
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    String type = StringArgumentType.getString(context, "type");
                    String path = StringArgumentType.getString(context, "path");
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                    switch (type) {
                        case "mp4":
                            displayInstance.playMP4(path);
                            source.sendMessage(Component.text("Playing mp4 video " + path, NamedTextColor.GRAY));
                            return 1;
                        case "mkv":
                            displayInstance.playMP4(path);
                            source.sendMessage(Component.text("Playing mkv video " + path, NamedTextColor.GRAY));
                            return 1;
                        case "youtube":
                            displayInstance.playYoutube(path);
                            source.sendMessage(Component.text("Playing Youtube video " + path, NamedTextColor.GRAY));
                            return 1;
                            default:
                            source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    }
                    return 0;
                });
    }
}
