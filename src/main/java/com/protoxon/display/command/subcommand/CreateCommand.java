package com.protoxon.display.command.subcommand;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.protoxon.display.Display;
import com.protoxon.display.DisplayInstance;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CreateCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("create")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    User user = PacketEvents.getAPI().getPlayerManager().getUser(source);
                    Location location = new Location(new Vector3d(0, 0.3, 0), 0f, 0f);
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.createDisplay((Player) source, location);
                    displayInstance.create();
                    displayInstance.setText("Hello World!");
                    source.sendMessage(Component.text("Created Display", NamedTextColor.GREEN));
                    return 1;
                });
    }
}
