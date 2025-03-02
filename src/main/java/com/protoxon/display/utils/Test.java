package com.protoxon.display.utils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.net.URLEncoder;
import java.util.List;
import java.util.Random;


public class Test {
    public static void main(String[] args) {
        try {
            String apiKey = "AIzaSyAMS0BglBYT3it08mJUCdDqlUiaS65r8m8";
            String query = "#Shorts"; // your query, e.g., looking for Shorts
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlString = "https://www.googleapis.com/youtube/v3/search?part=id,snippet" +
                    "&q=" + encodedQuery +
                    "&videoDuration=short" +
                    "&maxResults=10" +
                    "&key=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read response
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder responseBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // For now, simply print the raw JSON response
            System.out.println(responseBuilder.toString());

            // You can then parse the JSON using a library like Jackson or Gson.
            // For example, with Gson:
            // JsonObject json = JsonParser.parseString(responseBuilder.toString()).getAsJsonObject();
            // Process the JSON as needed.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
