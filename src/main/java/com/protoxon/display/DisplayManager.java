package com.protoxon.display;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.velocitypowered.api.proxy.Player;

import java.util.HashMap;

public class DisplayManager {

    public HashMap<Player, DisplayInstance> displays = new HashMap<>();

    /**
     * Retrieve a display from the database
     * @param player the viewer
     * @return A DisplayInstance
     */
    public DisplayInstance getDisplay(Player player) {
        return displays.get(player);
    }

    /**
     * Add a display to the database
     * @param player the viewer
     * @param displayInstance the display to add
     */
    private void addDisplay(Player player, DisplayInstance displayInstance) {
        displays.put(player, displayInstance);
    }

    /**
     * Creates a display instance for a player at the given location
     * @param player the viewer
     * @param location the location to spawn the display
     * @return The DisplayInstance that was created
     */
    public DisplayInstance createDisplay(Player player, Location location) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        DisplayInstance displayInstance = new DisplayInstance(user, location);
        addDisplay(player, displayInstance);
        return displayInstance;
    }
}
