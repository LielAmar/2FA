package com.lielamar.auth.database;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface AuthenticationDatabase {

    // Cached keys of players
    Map<UUID, String> cachedKeys = new HashMap<>();

    /**
     * Sets up the database
     *
     * @return   Whether or not setup was successful
     */
    boolean setup();


    /**
     * Sets the Secret Key of the player who's UUID is uuid
     *
     * @param uuid        UUID of the player to set the Secret Key of
     * @param secretKey   Secret Key to set
     * @return            The set Secret Key
     */
    String setSecretKey(UUID uuid, String secretKey);

    /**
     * Returns the Secret Key of the player who's UUID is uuid
     *
     * @param uuid   UUID of the player to get the Secret Key of
     * @return       Secret Key of the player
     */
    String getSecretKey(UUID uuid);

    /**
     * Checks whether or not a player who's UUID is uuid has a Secret Key
     *
     * @param uuid   UUID of the player to check the Secret Key of
     * @return       Whether or not the player has a Secret Key
     */
    boolean hasSecretKey(UUID uuid);

    /**
     * Removes the Secret Key of the player who's UUID is uuid
     *
     * @param uuid   UUID of the player to remove the Secret Key of
     */
    void removeSecretKey(UUID uuid);


    String setLastIP(UUID uuid, String lastIP);

    String getLastIP(UUID uuid);

    boolean hasLastIP(UUID uuid);

    void removeLastIP(UUID uuid);
}
