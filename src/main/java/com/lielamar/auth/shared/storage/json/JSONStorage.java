package com.lielamar.auth.shared.storage.json;

import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.storage.StorageType;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;

public class JSONStorage extends StorageHandler {

    private final File dir;

    public JSONStorage(String path) {
        this.dir = new File(path + "/auth/");
        this.dir.mkdir();
    }


    /**
     * Returns the JSON file of a player's uuid
     *
     * @param uuid Player's uuid to get the file of
     * @return JSON file
     */
    public File getFile(UUID uuid) {
        return createFile(uuid);
    }

    /**
     * Checks if a JSON file of player exists. If not, it creates a new one.
     *
     * @param uuid UUID of the player to create the file for
     * @return Created file
     */
    private File createFile(UUID uuid) {
        File file = new File(this.dir, uuid.toString() + ".json");

        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("key", JSONObject.NULL);
                    jsonObject.put("ip", JSONObject.NULL);
                    JSONUtils.write(jsonObject, new FileOutputStream(file));
                }
            } else {
                JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));
                boolean changed = false;
                if (!jsonObject.has("key")) {
                    jsonObject.put("key", JSONObject.NULL);
                    changed = true;
                }
                if (!jsonObject.has("ip")) {
                    jsonObject.put("ip", JSONObject.NULL);
                    changed = true;
                }

                if (changed)
                    JSONUtils.write(jsonObject, new FileOutputStream(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    @Override
    public String setKey(UUID uuid, String key) {
        try {
            File file = getFile(uuid);
            JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

            if (key == null) jsonObject.put("key", JSONObject.NULL);
            else jsonObject.put("key", key);

            JSONUtils.write(jsonObject, new FileOutputStream(file));

            return key;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getKey(UUID uuid) {
        try {
            File file = getFile(uuid);
            JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

            if (!jsonObject.has("key")) return null;
            Object key = jsonObject.get("key");
            if (key == JSONObject.NULL) return null;

            return key.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasKey(UUID uuid) {
        return getKey(uuid) != null;
    }

    @Override
    public void removeKey(UUID uuid) {
        setKey(uuid, null);
    }


    @Override
    public String setIP(UUID uuid, String ip) {
        try {
            File file = getFile(uuid);
            JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

            if (ip == null) jsonObject.put("ip", JSONObject.NULL);
            else jsonObject.put("ip", ip);

            JSONUtils.write(jsonObject, new FileOutputStream(file));

            return ip;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getIP(UUID uuid) {
        try {
            File file = getFile(uuid);
            JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

            if (!jsonObject.has("ip")) return null;
            Object ip = jsonObject.get("ip");
            if (ip == JSONObject.NULL) return null;

            return ip.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasIP(UUID uuid) {
        return getIP(uuid) != null;
    }
}
