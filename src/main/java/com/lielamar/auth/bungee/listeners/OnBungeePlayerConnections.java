package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.TwoFactorAuthentication;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class OnBungeePlayerConnections implements Listener {

    private final TwoFactorAuthentication plugin;
    public OnBungeePlayerConnections(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        this.plugin.getAuthHandler().playerQuit(player.getUniqueId());
    }
}
