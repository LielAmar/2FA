package com.lielamar.auth.database.json;

import com.lielamar.auth.Main;
import com.lielamar.auth.database.AuthenticationDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;

public class AuthJSON implements AuthenticationDatabase {

    private final Main main;

    private File dir;

    public AuthJSON(Main main) {
        this.main = main;
    }


    /**
     * Sets up the auth directory
     *
     * @return   Whether or not set up was successful
     */
    public boolean setup() {
        this.dir = new File(this.main.getDataFolder() + "/auth/");
        return this.dir.exists() || this.dir.mkdir();
    }

    /**
     * Returns the JSON file of a player's uuid
     *
     * @param uuid   Player's uuid to get the file of
     * @return       JSON file
     */
    public File getFile(UUID uuid) {
        return createFile(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     *  Checks if a JSON file of player exists. If not, it creates a new one.
     *  Checks if the file contains the property "secret_key" and if it doesn't, it sets it.
     *
     * @param player   Player to create the file for
     * @return         Created file
     */
    private File createFile(OfflinePlayer player) {
        File file = new File(this.dir, player.toString());

        try {
            if(!file.exists()) {
                if(file.createNewFile()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("secret_key", JSONObject.NULL);
                    JSONUtils.write(jsonObject, new FileOutputStream(file));
                }
            } else {
                JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));
                if(!jsonObject.has("secret_key")) {
                    jsonObject.put("secret_key", JSONObject.NULL);
                    JSONUtils.write(jsonObject, new FileOutputStream(file));
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    @Override
    public String setSecretKey(UUID uuid, String secretKey) {
        try {
            File file = getFile(uuid);
            JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

            if(secretKey == null) jsonObject.put("secret_key", JSONObject.NULL);
            else jsonObject.put("secret_key", secretKey);

            JSONUtils.write(jsonObject, new FileOutputStream(file));

            AuthenticationDatabase.cachedKeys.put(uuid, secretKey);

            return secretKey;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    @Override
    public String getSecretKey(UUID uuid) {
        if(AuthenticationDatabase.cachedKeys.containsKey(uuid))
            return AuthenticationDatabase.cachedKeys.get(uuid);

        try {
            File file = getFile(uuid);
            JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

            if(!jsonObject.has("secret_key")) return null;
            Object key = jsonObject.get("secret_key");
            if(key == JSONObject.NULL) return null;

            AuthenticationDatabase.cachedKeys.put(uuid, key.toString());

            return key.toString();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasSecretKey(UUID uuid) {
        return getSecretKey(uuid) != null;
    }

    @Override
    public void removeSecretKey(UUID uuid) {
        setSecretKey(uuid, null);
    }
}