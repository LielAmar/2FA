package com.lielamar.auth.bukkit.utils.uuid;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lielamar.auth.bukkit.utils.fetching.FetchingUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UUIDUtils {

    public static final long STARTUP_TIMESTAMP = System.currentTimeMillis();
    private static final Type gsonType = new TypeToken<Map<String, String>>() {}.getType();

    /**
     * Generates a UUID based on current timestamp & startup time
     *
     * @return   Generated time-based uuid
     */
    public static @NotNull UUID generateTimeBasedUUID() { return generateTimeBasedUUID(STARTUP_TIMESTAMP); }
    public static @NotNull UUID generateTimeBasedUUID(long timestamp) { return new UUID(System.currentTimeMillis(), timestamp); }


    /**
     * Fetches a Minecraft user's UUID from Mojang's API
     *
     * @param username                    Username of the Minecraft user
     * @param callback                    Callback to call when a response is received
     * @throws InvalidResponseException   Throws an exception if the given response was invalid
     */
    public static void fetchUUIDFromMojang(@NotNull String username, @NotNull UUIDCallback callback) throws InvalidResponseException {
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(username);

        if(offlinePlayer != null)
            callback.run(offlinePlayer.getUniqueId());
        else {
            FetchingUtils.fetch(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username), (response) -> {
                Map<String, String> res = new Gson().fromJson(response, gsonType);

                String uuid = res.getOrDefault("id", null);
                callback.run(uuid == null ? null : UUID.fromString(uuid));
            });
        }
    }

    /**
     * Fetches a Minecraft user's UUID from Mojang's API
     *
     * @param username   Username of the Minecraft user
     * @return           A CompletableFuture object of the user's uuid
     */
    public static @NotNull CompletableFuture<UUID> fetchUUIDFromMojang(@NotNull String username) {
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(username);

        if(offlinePlayer != null)
            return CompletableFuture.supplyAsync(offlinePlayer::getUniqueId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = FetchingUtils.fetch(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username));

                try {
                    Map<String, String> res = new Gson().fromJson(response, gsonType);
                    String uuid = res.getOrDefault("id", null);
                    if(uuid == null) {
                        throw new CompletionException(new UUIDNotFoundException("UUID for username " + username + " was not found!"));
                    }

                    return UUID.fromString(fixUUID(uuid));
                } catch (Exception exception) {
                    throw new CompletionException(new UUIDNotFoundException("UUID for username " + username + " was not found!"));
                }
            } catch (InvalidResponseException exception) {
                exception.printStackTrace();
                throw new CompletionException(exception);
            }
        });
    }

    /**
     * Fixes a uuid (adds dashes)
     *
     * @param uuid   UUID to fix
     * @return       Fixed uuid
     */
    public static @NotNull String fixUUID(@NotNull String uuid) {
        if(uuid.contains("-"))
            return uuid;

        return uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
    }
}