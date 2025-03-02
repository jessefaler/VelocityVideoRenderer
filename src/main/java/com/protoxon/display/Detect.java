package com.protoxon.display;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Detect implements PacketListener {
    int clicks;
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // The user represents the player.
        User user = event.getUser();
        // Identify what kind of packet it is.
        if (event.getPacketType() != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) return;
        DisplayInstance displayInstance = Display.DISPLAY_MANAGER.getDisplay(Display.proxy.getPlayer(user.getProfile().getUUID()).get());
        if(clicks != 0) {
            displayInstance.stopPlaying();
        }
        clicks++;
        switch (clicks) {
            case 1:
                displayInstance.playFromURL("https://www.youtube.com/shorts/twx0rf-ZuR4");
                return;
            case 2:
                displayInstance.playFromURL("https://www.youtube.com/shorts/QEHwAylWuFw");
                return;
            case 3:
                displayInstance.playFromURL("https://www.youtube.com/shorts/Ayy8vG9EMFY");
                return;
            case 4:
                displayInstance.playFromURL("https://www.youtube.com/shorts/OqVPJMR7wIE");
                return;
            case 5:
                displayInstance.playFromURL("https://www.youtube.com/shorts/LH5R3YGGm-A");
                return;
            case 6:
                displayInstance.playFromURL("https://www.youtube.com/shorts/SVQKulhuckA");
                return;
            case 7:
                displayInstance.playFromURL("https://www.youtube.com/shorts/YaKgoEuCXv0");
                return;
            case 8:
                displayInstance.playFromURL("https://www.youtube.com/shorts/mSADd_WCJU8");
                clicks = 0;
        }
    }

    /**
     * Fetches a YouTube Short video URL suggestion.
     * @return A string containing the YouTube Short URL.
     */
    public static String getYouTubeShortSuggestion() {
        try {
            // Construct the API request URL
            String urlString = "youtube.googleapis.com" + "?part=snippet&type=video&videoDuration=short&maxResults=1&key=" + "AIzaSyAMS0BglBYT3it08mJUCdDqlUiaS65r8m8";

            // Open a connection and send the GET request
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            String videoId = jsonResponse.getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("id")
                    .getString("videoId");

            // Construct the YouTube Shorts URL
            String videoUrl = "https://www.youtube.com/shorts/" + videoId;

            // Return the video URL
            return videoUrl;

        } catch (Exception e) {
            // Handle errors (e.g., network or API issues)
            System.err.println("Error fetching YouTube Shorts: " + e.getMessage());
            return null;  // Return null if there was an error
        }
    }

    public static void main(String[] args) {
        System.out.println(getYouTubeShortSuggestion());
    }
}
