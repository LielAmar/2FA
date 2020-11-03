package com.lielamar.auth.authentication;

import com.lielamar.auth.Main;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class AuthenticationManager {

    private final Main main;
    private final Set<UUID> lockedPlayers; // Players who await authentication
    private final Map<UUID, Integer> failedAttempts; // Players who failed to insert the code

    public AuthenticationManager(Main main) {
        this.main = main;

        this.lockedPlayers = new HashSet<>();
        this.failedAttempts = new HashMap<>();
    }

    public boolean isLocked(Player player) {
        return this.lockedPlayers.contains(player.getUniqueId());
    }
    public void lockPlayer(Player player) { this.lockedPlayers.add(player.getUniqueId()); }
    public void unlockPlayer(Player player) { this.lockedPlayers.remove(player.getUniqueId()); }

    public boolean hasAuthentication(Player player) {
        return this.main.getAuthDatabaseManager().getDatabase().hasSecretKey(player.getUniqueId());
    }

    public boolean authenticate(Player player, int code) {
        String secretKey = this.main.getAuthDatabaseManager().getDatabase().getSecretKey(player.getUniqueId());

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean isValid = gAuth.authorize(secretKey, code);

        if(isValid)
            this.lockedPlayers.remove(player.getUniqueId());
        else
            this.failAttempt(player);

        return isValid;
    }

    public void failAttempt(Player player) {
        Integer attempts = this.failedAttempts.get(player.getUniqueId());
        if(attempts == null) attempts = 0;
        attempts++;

        this.failedAttempts.put(player.getUniqueId(), attempts);

        if(attempts > 3) {
            Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission("2fa.alert")).forEach(pl ->
                    pl.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "(!) " + ChatColor.YELLOW + player.getName() +
                                      ChatColor.GRAY + " failed to authenticate 3 times!"));
        }
    }
}