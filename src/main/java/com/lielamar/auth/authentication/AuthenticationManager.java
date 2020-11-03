package com.lielamar.auth.authentication;

import com.lielamar.auth.Main;
import org.bukkit.entity.Player;

import java.util.*;

public class AuthenticationManager {

    private Main main;

    // Players who await authentication.
    private Set<UUID> authenticators;

    public AuthenticationManager(Main main) {
        this.main = main;

        this.authenticators = new HashSet<UUID>();
    }

    public boolean isWaitingForAuthentication(Player player) {
        return authenticators.contains(player.getUniqueId());
    }

    public boolean hasAuthentication(Player player) {
        return this.main.getAuthDatabase().hasSecretKey(player.getUniqueId());
    }

    public boolean authenticate(Player player, String code) {

        return false;
    }
}