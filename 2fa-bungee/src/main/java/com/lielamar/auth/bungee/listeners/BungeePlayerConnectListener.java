package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.handlers.BungeeAuthHandler;
import jakarta.inject.Inject;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class BungeePlayerConnectListener implements Listener {
    private final BungeeAuthHandler authHandler;

    @Inject
    public BungeePlayerConnectListener(final @NotNull BungeeAuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        authHandler.playerQuit(event.getPlayer().getUniqueId());
    }
}
