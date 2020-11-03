package com.lielamar.auth.authentication;

import com.lielamar.auth.Main;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.entity.Player;

import java.util.*;

public class AuthenticationManager {

    private Main main;

    // Players who await authentication.
    private Set<UUID> lockedPlayers;

    public AuthenticationManager(Main main) {
        this.main = main;

        this.lockedPlayers = new HashSet<>();
    }

    public boolean isLocked(Player player) {
        return lockedPlayers.contains(player.getUniqueId());
    }

    public boolean hasAuthentication(Player player) {
        return this.main.getAuthDatabase().hasSecretKey(player.getUniqueId());
    }

    public boolean authenticate(Player player, int code) {
        String secretKey = this.main.getAuthDatabase().getSecretKey(player.getUniqueId());

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean isValid = gAuth.authorize(secretKey, code);

        if(isValid)
            lockedPlayers.remove(player.getUniqueId());

        return isValid;
    }
}