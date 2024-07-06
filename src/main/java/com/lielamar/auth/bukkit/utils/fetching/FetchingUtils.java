package com.lielamar.auth.bukkit.utils.fetching;

import com.lielamar.auth.bukkit.utils.uuid.InvalidResponseException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchingUtils {

    /**
     * Fetches a GET request to the given URL
     *
     * @param url        URL to fetch data from
     * @param callback   Callback to call when a response is received
     */
    public static void fetch(@NotNull String url, @NotNull JSONCallback callback) throws InvalidResponseException {
        callback.run(fetch(url));
    }

    public static @NotNull String fetch(@NotNull String url) throws InvalidResponseException {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            if(response.length() == 0)
                throw new InvalidResponseException("Returned data from the GET request was empty!");

            return response.toString();
        } catch(IOException exception) {
            throw new InvalidResponseException("Fetching " + url + " failed for the following reason: " + exception.toString());
        }
    }
}