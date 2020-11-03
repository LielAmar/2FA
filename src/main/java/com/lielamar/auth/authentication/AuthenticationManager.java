package com.lielamar.auth.authentication;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class AuthenticationManager {

    /**
     * Players who await authentication.
     */
    private Set<UUID> authenticators;

    private Plugin plugin;

    public AuthenticationManager(Plugin plugin) {
        this.authenticators = new HashSet<UUID>();
        this.plugin = plugin;
    }

    public boolean isWaitingForAuth(Player player) {
        return authenticators.contains(player.getUniqueId());
    }
}
