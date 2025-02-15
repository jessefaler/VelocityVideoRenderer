package com.protoxon.display;

import java.util.*;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import net.kyori.adventure.text.Component;

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
    byte flags;
    DisplayPipeline displayWorker = new DisplayPipeline(this);

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
        // Set Default Values
        setLineWidth(100000);
        setBackgroundColor(4278190080L); // Black & No Opacity
        setIsSeeThrough(false);
    }

    /**
     * Sets the maximum number of pixels to display in each frame
     * resizes the image to fit withing the pixel count
     * @param maxPixels the max number of pixels
     */
    public void setMaxPixels(int maxPixels) {
        displayWorker.maxPixels = maxPixels;
    }

    /**
     * Plays an MP4 file
     * @param path the path to the MP4 file
     */
    public void playMP4(String path) {
        displayWorker.play(path);
    }

    /**
     * Plays a youtube video
     * @param url the url to the youtube video
     */
    public void playYoutube(String url) {
        //TODO implement playing from youtube
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

    /**
     * Sets the text on the text display <p>
     * <a href="https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Entity_metadata#Text_Display">Text Display Data Information</a>
     * @param text the text to set
     */
    public void setText(String text) {
        // Create the text component
        Component textComponent = Component.text(text);
        // Create the entity data containing the text component
        EntityData textMetadata = new EntityData(23, EntityDataTypes.ADV_COMPONENT, textComponent);
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        user.sendPacket(packet);
    }

    /**
     * Sets the text on the text display <p>
     * <a href="https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Entity_metadata#Text_Display">Text Display Data Information</a>
     * @param component the text componet to set
     */
    public void setText(Component component) {
        // Create the entity data containing the text component
        EntityData textMetadata = new EntityData(23, EntityDataTypes.ADV_COMPONENT, component);
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        user.sendPacket(packet);
    }

    /**
     * Sets the text opacity on the text display. The opacity value is interpreted as follows:
     * <p>
     * - The opacity value ranges from 0 to 255.
     * - Since there are no unsigned bytes in NBT, values greater than 127 are mapped to an alpha value of `opacity - 256`,
     *   resulting in values from -128 to 127.
     * - Values less than 26 are discarded by the client.
     * - Default behavior sets opacity to `-1`, meaning fully opaque (255).
     * - The opacity is interpolated accordingly to ensure smooth transitions.
     * </p>
     *
     * @param opacity the opacity value to set (0 to 255)
     */
    public void setTextOpacity(int opacity) {
        // Adjust the opacity value according to the rules
        byte finalOpacity;
        if (opacity > 127) {
            finalOpacity = (byte) (opacity - 256);  // Alpha-256 mapping (from 128-255 -> -128 to -1)
        } else {
            finalOpacity = (byte) opacity;  // No change needed for values between 26 and 127
        }
        // Create the entity data containing the text opacity value
        EntityData textMetadata = new EntityData(26, EntityDataTypes.BYTE, finalOpacity);
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        // Send the packet to the user
        user.sendPacket(packet);
    }

    /**
     * Sets the text background color on the text display.
     *
     * @param color the color in ARGB format
     */
    public void setBackgroundColor(long color) {
        // Create the entity data containing background ARGB color value
        EntityData textMetadata = new EntityData(25, EntityDataTypes.INT, (int) color); // Cast to int for packet
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        // Send the packet to the user
        user.sendPacket(packet);
    }

    /**
     * Sets the "is see-through" flag in the flags bitmask.
     * If {@code seeThrough} is {@code true}, the flag is set to 1; otherwise, it is set to 0.
     *
     * @param seeThrough {@code true} to enable see-through, {@code false} to disable it.
     */
    public void setIsSeeThrough(boolean seeThrough) {
        // The second bit is the is see through flag
        if (seeThrough) {
            flags = (byte) (flags | 0x02);  // Set second bit to 1
        } else {
            flags = (byte) (flags & ~0x02); // Clear second bit to 0
        }
        updateFlags(flags); // Update the entity with the new flags
    }

    /**
     * Sets the "Use default background color" flag in the flags bitmask.
     * If {@code useDefaultBackgroundColor} is {@code true}, the flag is set to 1; otherwise, it is set to 0.
     *
     * @param useDefaultBackgroundColor {@code true} to enable the default background color, {@code false} to disable it.
     */
    public void setUseDefaultBackgroundColor(boolean useDefaultBackgroundColor) {
        // The second bit is the is use default background color flag
        if (useDefaultBackgroundColor) {
            flags = (byte) (flags | 0x04);  // Set second bit to 1
        } else {
            flags = (byte) (flags & ~0x04); // Clear second bit to 0
        }
        updateFlags(flags); // Update the entity with the new flags
    }

    /**
     * Sets the "Has Shadow" flag in the flags bitmask.
     * If {@code hasShadow} is {@code true}, the flag is set to 1; otherwise, it is set to 0.
     *
     * @param hasShadow {@code true} to enable the shadow, {@code false} to disable it.
     */
    public void setHasShadow(boolean hasShadow) {
        // The second bit is the is use default background color flag
        if (hasShadow) {
            flags = (byte) (flags | 0x01);  // Set second bit to 1
        } else {
            flags = (byte) (flags & ~0x01); // Clear second bit to 0
        }
        updateFlags(flags); // Update the entity with the new flags
    }

    /**
     * Sets the flags byte.
     *
     * @param flags the byte value representing the flags to be set.
     */
    private void updateFlags(byte flags) {
        // Create the entity data containing background ARGB color value
        EntityData textMetadata = new EntityData(27, EntityDataTypes.BYTE, flags); // Cast to int for packet
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        // Send the packet to the user
        user.sendPacket(packet);
    }

    /**
     * Stops playing a video
     */
    public void stopPlaying() {
        displayWorker.scheduler.shutdown();
    }

    /**
     * Sets the text displays line width
     * @param width the line width
     */
    public void setLineWidth(int width) {
        // Create the entity data containing the text component
        EntityData textMetadata = new EntityData(24, EntityDataTypes.INT, width);
        // Create the metadata packet
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(textMetadata));
        user.sendPacket(packet);
    }
}
