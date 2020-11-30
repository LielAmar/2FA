package com.lielamar.auth.shared.handlers;

import com.lielamar.auth.shared.storage.StorageHandler;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AuthHandler {

    /**
     * This class was originally written by Connor Linfoot (https://github.com/ConnorLinfoot/MC2FA).
     * This class was edited by Liel Amar to add Bungeecord, JSON, MySQL, and MongoDB support.
     */

    protected StorageHandler storageHandler;
    protected HashMap<UUID, AuthState> authStates = new HashMap<>();
    private final HashMap<UUID, String> pendingKeys = new HashMap<>();
    private final Map<UUID, Integer> failedAttempts = new HashMap<>();

    public enum AuthState {
        DISABLED, PENDING_SETUP, PENDING_LOGIN, AUTHENTICATED
    }


    /**
     * Returns a Player's Auth State
     *
     * @param uuid   UUID of the player to get the Auth State of
     * @return       Auth State
     */
    public AuthState getAuthState(UUID uuid) {
        if(authStates.containsKey(uuid))
            return authStates.get(uuid);
        return null;
    }

    /**
     * Checks if a player has a Secret Key
     *
     * @param uuid   UUID of the player to check
     * @return       Whether or not the player has a Secret Key
     */
    public boolean is2FAEnabled(UUID uuid) {
        if(!authStates.containsKey(uuid))
            return false;
        return authStates.get(uuid).equals(AuthState.PENDING_LOGIN) || authStates.get(uuid).equals(AuthState.AUTHENTICATED);
    }

    /**
     * Checks if a player is Pending 2FA Setup
     *
     * @param uuid   UUID of the player to check
     * @return       Whether or not the player is pending setup
     */
    public boolean isPendingSetup(UUID uuid) {
        return authStates.get(uuid).equals(AuthState.PENDING_SETUP);
    }

    /**
     * Creates a Key for a player
     *
     * @param uuid   UUID of the player to create the Secret Key for
     * @return       Created Key
     */
    public String createKey(UUID uuid) {
        GoogleAuthenticator authenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = authenticator.createCredentials();

        changeState(uuid, AuthState.PENDING_SETUP);
        pendingKeys.put(uuid, key.getKey());
        return key.getKey();
    }

    /**
     * Validates a code with the player's key
     *
     * @param uuid   UUID of the player to validate the key of
     * @param code   Inserted code
     * @return       Whether or not the code is valid
     */
    public boolean validateKey(UUID uuid, Integer code) {
        String key = getKey(uuid);
        if(key != null && new GoogleAuthenticator().authorize(key, code)) {
            changeState(uuid, AuthState.AUTHENTICATED);
            return true;
        }
        return false;
    }

    /**
     * Approves a key and storing it
     *
     * @param uuid   UUID of the player to validate the key of
     * @param code   Inserted code
     * @return       Whether or not the code is valid
     */
    public boolean approveKey(UUID uuid, Integer code) {
        String key = getPendingKey(uuid);
        if(key != null && new GoogleAuthenticator().authorize(key, code)) {
            changeState(uuid, AuthState.AUTHENTICATED);
            getStorageHandler().setKey(uuid, key);
            pendingKeys.remove(uuid);
            return true;
        }
        return false;
    }

    /**
     * Resets a player's key
     *
     * @param uuid   UUID of the player to reset the key of
     */
    public void resetKey(UUID uuid) {
        pendingKeys.remove(uuid);
        changeState(uuid, AuthState.DISABLED);
        getStorageHandler().removeKey(uuid);
    }

    /**
     * Returns a player's key
     *
     * @param uuid   UUID of the player to get the key of
     * @return       Player's key
     */
    private String getKey(UUID uuid) {
        if(!is2FAEnabled(uuid))
            return null;
        return getStorageHandler().getKey(uuid);
    }

    /**
     * Returns a player's pending key (if they have one)
     *
     * @param uuid   UUID of the player to get the pending key of
     * @return       Player's pending key
     */
    protected String getPendingKey(UUID uuid) {
        if(!isPendingSetup(uuid))
            return null;
        return pendingKeys.get(uuid);
    }

    /**
     * Checks if a player needs to authenticate
     *
     * @param uuid   UUID of the player to check
     * @return       Whether or not the player needs to authenticate
     */
    public boolean needsToAuthenticate(UUID uuid) {
        return is2FAEnabled(uuid) && !authStates.get(uuid).equals(AuthState.AUTHENTICATED);
    }

    /**
     * Returns the amount of times a player has failed authentication
     *
     * @param uuid   UUID of the player to check
     * @return       Amount of fails
     */
    public int increaseFailedAttempts(UUID uuid, int amount) {
        if(!failedAttempts.containsKey(uuid)) {
            failedAttempts.put(uuid, amount);
            return amount;
        } else {
            int playerFailedAttempts = failedAttempts.get(uuid) + amount;
            failedAttempts.put(uuid, playerFailedAttempts);
            return playerFailedAttempts;
        }
    }

    /**
     * Returns a QR Code URL based on a player's key
     *
     * @param urlTemplate   Base URL of the QR Code
     * @param uuid          UUID of the player to get the key of
     * @return              URL of the QR Code
     */
    public String getQRCodeURL(String urlTemplate, UUID uuid) {
        return null;
    }

    public void playerJoin(UUID uuid) {
    }

    public void playerQuit(UUID uuid) {
        pendingKeys.remove(uuid);
        authStates.remove(uuid);
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public abstract void changeState(UUID uuid, AuthState authState);
}