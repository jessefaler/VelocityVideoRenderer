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

public class SetCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("set")
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
                    builder.suggest("text");
                    builder.suggest("opacity");
                    builder.suggest("background_color");
                    builder.suggest("has_shadow");
                    builder.suggest("use_default_background_color");
                    builder.suggest("see_through");
                    builder.suggest("line_width");
                    builder.suggest("max_pixels");
                    return builder.buildFuture();
                })
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(Component.text("Invalid command usage", NamedTextColor.RED));
                    return 0;
                })
                .then(value());
    }

    private static RequiredArgumentBuilder<CommandSource, String> value() {
        return RequiredArgumentBuilder.<CommandSource, String>argument("value", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    String type = StringArgumentType.getString(context, "type");
                    if(type.equals("has_shadow") || type.equals("use_default_background_color") || type.equals("see_through")) {
                        builder.suggest("true");
                        builder.suggest("false");
                    }
                    return builder.buildFuture();
                })
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                    if(displayInstance == null) {
                        player.sendMessage(Component.text("You do not have a display", NamedTextColor.RED));
                        return 0;
                    }
                    String type = StringArgumentType.getString(context, "type");
                    String value = StringArgumentType.getString(context, "value");
                    switch (type) {
                        case "text":
                            displayInstance.setText(value);
                            source.sendMessage(Component.text("Set text to:" + value, NamedTextColor.RED));
                            return 1;
                        case "opacity":
                            int opacity = Integer.parseInt(value);
                            displayInstance.setTextOpacity(opacity);
                            player.sendMessage(Component.text("Set Opacity to: " + opacity, NamedTextColor.GRAY));
                            return 1;
                        case "line_width":
                            int width = Integer.parseInt(value);
                            displayInstance.setLineWidth(width);
                            player.sendMessage(Component.text("Set line width to: " + width, NamedTextColor.GRAY));
                            return 1;
                        case "background_color":
                            long color = Long.parseLong(value);
                            displayInstance.setBackgroundColor(color);
                            player.sendMessage(Component.text("Set background color to: " + color, NamedTextColor.GRAY));
                            return 1;
                        case "has_shadow":
                            if(value.equals("true")) {
                                displayInstance.setHasShadow(true);
                                player.sendMessage(Component.text("Set has shadow to true", NamedTextColor.GRAY));
                                return 1;
                            }
                            if(value.equals("false")) {
                                player.sendMessage(Component.text("Set has shadow to false", NamedTextColor.GRAY));
                                displayInstance.setHasShadow(false);
                                return 1;
                            }
                            player.sendMessage(Component.text("Invalid command argument" + value, NamedTextColor.RED));
                            player.sendMessage(Component.text("Required type true or false", NamedTextColor.RED));
                            return 1;
                        case "use_default_background_color":
                            if(value.equals("true")) {
                                displayInstance.setUseDefaultBackgroundColor(true);
                                player.sendMessage(Component.text("Set use default background color to true", NamedTextColor.GRAY));
                                return 1;
                            }
                            if(value.equals("false")) {
                                player.sendMessage(Component.text("Set use default background color to false", NamedTextColor.GRAY));
                                displayInstance.setUseDefaultBackgroundColor(false);
                                return 1;
                            }
                            player.sendMessage(Component.text("Invalid command argument" + value, NamedTextColor.RED));
                            player.sendMessage(Component.text("Required type true or false", NamedTextColor.RED));
                            return 1;
                        case "see_through":
                            if(value.equals("true")) {
                                displayInstance.setIsSeeThrough(true);
                                player.sendMessage(Component.text("Set is see through to true", NamedTextColor.GRAY));
                                return 1;
                            }
                            if(value.equals("false")) {
                                player.sendMessage(Component.text("Set is see through to false", NamedTextColor.GRAY));
                                displayInstance.setIsSeeThrough(false);
                                return 1;
                            }
                            player.sendMessage(Component.text("Invalid command argument" + value, NamedTextColor.RED));
                            player.sendMessage(Component.text("Required type true or false", NamedTextColor.RED));
                            return 1;
                        case "max_pixels":
                            int maxPixels = Integer.parseInt(value);
                            displayInstance.setMaxPixels(maxPixels);
                            player.sendMessage(Component.text("Set max pixels to: " + maxPixels, NamedTextColor.GRAY));
                            return 1;
                        default:
                            player.sendMessage(Component.text("Invalid command argument" + value, NamedTextColor.RED));
                    }
                    return 0;
                });
    }
}
