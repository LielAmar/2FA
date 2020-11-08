package com.lielamar.auth.bungee.handlers;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthHandler {

    private final Set<UUID> authenticatedPlayers;

    public AuthHandler() {
        this.authenticatedPlayers = new HashSet<>();
    }

    public boolean containsPlayer(ProxiedPlayer player) {
        if(player == null) return false;
        return authenticatedPlayers.contains(player.getUniqueId());
    }

    public void setPlayer(ProxiedPlayer player) {
        authenticatedPlayers.add(player.getUniqueId());
    }

    public void removePlayer(ProxiedPlayer player) {
        authenticatedPlayers.remove(player.getUniqueId());
    }
}