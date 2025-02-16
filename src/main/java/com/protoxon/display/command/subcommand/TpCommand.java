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

public class TpCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("tp")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 1;
                })
                .then(x());
    }

    private static RequiredArgumentBuilder<CommandSource, String> x() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("x", StringArgumentType.string())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 0;
                })
                .then(y());
    }

    private static RequiredArgumentBuilder<CommandSource, String> y() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("y", StringArgumentType.string())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 0;
                })
                .then(z());
    }

    private static RequiredArgumentBuilder<CommandSource, String> z() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("z", StringArgumentType.string())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                    if(displayInstance == null) {
                        source.sendMessage(Component.text("You do not have a display", NamedTextColor.RED));
                        return 1;
                    }
                    String xPos = StringArgumentType.getString(context, "x");
                    String yPos = StringArgumentType.getString(context, "y");
                    String zPos = StringArgumentType.getString(context, "z");
                    float x = Float.parseFloat(xPos);
                    float y = Float.parseFloat(yPos);
                    float z = Float.parseFloat(zPos);
                    displayInstance.tp(x, y, z, 0, 0);
                    return 0;
                })
                .then(yaw());
    }

    private static RequiredArgumentBuilder<CommandSource, String> yaw() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("yaw", StringArgumentType.string())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 0;
                })
                .then(pitch());
    }

    private static RequiredArgumentBuilder<CommandSource, String> pitch() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("pitch", StringArgumentType.string())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                    if(displayInstance == null) {
                        source.sendMessage(Component.text("You do not have a display", NamedTextColor.RED));
                        return 1;
                    }
                    String xPos = StringArgumentType.getString(context, "x");
                    String yPos = StringArgumentType.getString(context, "y");
                    String zPos = StringArgumentType.getString(context, "z");
                    String yawS = StringArgumentType.getString(context, "yaw");
                    String pitchS = StringArgumentType.getString(context, "pitch");
                    float x = Float.parseFloat(xPos);
                    float y = Float.parseFloat(yPos);
                    float z = Float.parseFloat(zPos);
                    float yaw = Float.parseFloat(yawS);
                    float pitch = Float.parseFloat(pitchS);
                    displayInstance.tp(x, y, z, yaw, pitch);
                    return 0;
                });
    }
}
