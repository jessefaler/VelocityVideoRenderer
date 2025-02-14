package com.protoxon.display.command.subcommand;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.protoxon.display.DisplayInstance;
import com.velocitypowered.api.command.CommandSource;

public class CreateCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("create")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    User user = PacketEvents.getAPI().getPlayerManager().getUser(source);
                    Location location = new Location(new Vector3d(0, 0, 0), 0f, 0f);
                    DisplayInstance displayInstance = new DisplayInstance(user, location);
                    displayInstance.create();
                    displayInstance.setText("Hello World!");
                    source.sendPlainMessage("Created Display.");
                    return 1;
                });
    }
}
