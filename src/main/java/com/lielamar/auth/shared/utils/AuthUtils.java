package com.lielamar.auth.shared.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class AuthUtils {

    /**
     * Communicates with Mojang's API to get a player's uuid through their IGN
     *
     * @param name   Name to get the UUID of
     * @return       UUID of the player
     */
    public static UUID fetchUUID(String name) {
        try {
            String MOJANG_API = "https://api.mojang.com/users/profiles/minecraft/%s";
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(MOJANG_API, name)).openConnection();
            connection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JsonElement json = new JsonParser().parse(response.toString());
            String uuid = json.getAsJsonObject().get("id").getAsString();

            // Regex to add dashes to the uuid
            if(uuid.indexOf('-') == -1) {
                uuid = uuid.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
            }
            return UUID.fromString(uuid);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
