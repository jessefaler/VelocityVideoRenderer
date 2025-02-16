package com.protoxon.display.command.subcommand;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.protoxon.display.Display;
import com.protoxon.display.DisplayInstance;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class DeleteCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("delete")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    if(!Display.DISPLAY_MANAGER.displays.containsKey(player)) {
                        source.sendMessage(Component.text("You do not have a display.", NamedTextColor.RED));
                        return 1;
                    }
                    Display.DISPLAY_MANAGER.deleteDisplay(player);
                    source.sendMessage(Component.text("Deleted Display.", NamedTextColor.GREEN));
                    return 1;
                });
    }
}
