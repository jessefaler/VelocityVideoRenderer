package com.protoxon.display.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.protoxon.display.Display;
import com.protoxon.display.DisplayInstance;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ResumeCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("resume")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player player = (Player) source;
                    DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(player);
                    displayInstance.resume();
                    source.sendMessage(Component.text("Resumed Display.", NamedTextColor.GREEN));
                    return 1;
                });
    }
}
