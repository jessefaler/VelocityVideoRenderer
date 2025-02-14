package com.protoxon.display;

import com.github.retrooper.packetevents.PacketEvents;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * An instance of a display for a user
 * For protocol information visit
 * <a href="https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Entity_metadata#Text_Display">Text Display Data</a>
 * <a href="https://minecraft.wiki/w/Java_Edition_protocol">Java Protocol</a>
 */
public class DisplayInstance {

    Location location;
    User user;
    UUID uuid;
    int entityId;
    private static final Random random = new Random();

    /**
     * A Display instance
     * @param location the location to place the display
     * @param user the user who is viewing this display instance
     */
    public DisplayInstance(User user, Location location) {
        this.user = user;
        this.location = location;
        uuid = UUID.randomUUID(); // Generate a UUID for the entity
        entityId = random.nextInt(Integer.MAX_VALUE); // Generate a entityId for the entity
    }

    /**
     * Creates a text display
     */
    public void create() {
        spawn(user, location);
    }

    public void spawn(User user, Location location) {
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                entityId,
                uuid,
                EntityTypes.TEXT_DISPLAY,
                location,
                location.getYaw(), // Head yaw
                0, // No additional data
                null // We won't specify any initial velocity
        );
        user.sendPacket(packet);
    }

    public void setText(String text) {
        // Create the text componet
        Component textComponent = Component.text(text);
        // Create the entity data containing the text component
        EntityData textMetadata = new EntityData(23, EntityDataTypes.ADV_COMPONENT, textComponent);
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        user.sendPacket(packet);
    }

}
