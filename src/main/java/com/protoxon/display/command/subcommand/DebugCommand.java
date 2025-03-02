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

public class DebugCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("debug")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 1;
                })
                .then(toggle());
    }

    private static RequiredArgumentBuilder<CommandSource, String> toggle() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("toggle", StringArgumentType.string())
                .suggests((context, builder) -> {
                    builder.suggest("enable");
                    builder.suggest("disable");
                    return builder.buildFuture();
                })
                .executes(context -> {
                    CommandSource source = context.getSource();
                    String toggle = StringArgumentType.getString(context, "toggle");
                    Player player = (Player) source;
                    if(toggle.equals("enable")) {
                        DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                        displayInstance.enableDebugLogging(player);
                        source.sendMessage(Component.text("Enabled debug logging.", NamedTextColor.GREEN));
                        return 1;
                    }
                    if(toggle.equals("disable")) {
                        DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                        displayInstance.disableDebugLogging();
                        source.sendMessage(Component.text("disabled debug logging.", NamedTextColor.GREEN));
                        return 1;
                    }
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 0;
                });
    }
}
