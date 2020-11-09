package com.lielamar.auth.shared.storage;

import java.util.UUID;

public abstract class StorageHandler {

    /**
     * Returns the type of the storage
     *
     * @return   Type of the storage
     */
    public abstract StorageType getStorageType();

    /**
     * Sets the Key of the player who's UUID is uuid
     *
     * @param uuid        UUID of the player to set the Key of
     * @param secretKey   Key to set
     * @return            The set Key
     */
    public abstract String setKey(UUID uuid, String secretKey);

    /**
     * Returns the Key of the player who's UUID is uuid
     *
     * @param uuid   UUID of the player to get the Key of
     * @return       Key of the player
     */
    public abstract String getKey(UUID uuid);

    /**
     * Checks whether or not a player who's UUID is uuid has a Key
     *
     * @param uuid   UUID of the player to check the Key of
     * @return       Whether or not the player has a Key
     */
    public abstract boolean hasKey(UUID uuid);

    /**
     * Removes the Key of the player who's UUID is uuid
     *
     * @param uuid   UUID of the player to remove the Key of
     */
    public abstract void removeKey(UUID uuid);


    /**
     * Sets the Last IP of the player who's UUID is uuid
     *
     * @param uuid     UUID of the player to set the IP of
     * @param lastIP   IP to set
     * @return         The set IP
     */
    public abstract String setIP(UUID uuid, String lastIP);

    /**
     * Returns the IP of the player who's UUID is uuid
     *
     * @param uuid   UUID of the player to get the IP of
     * @return       Last IP of the player
     */
    public abstract String getIP(UUID uuid);

    /**
     * Checks whether or not a player who's UUID is uuid has a Last IP
     *
     * @param uuid   UUID of the player to check the IP of
     * @return       Whether or not the player has a Last IP
     */
    public abstract boolean hasIP(UUID uuid);
}