package com.lielamar.auth.velocity.listeners;

import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public class OnVelocityPlayerConnections {

    private final TwoFactorAuthentication plugin;

    public OnVelocityPlayerConnections(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();

        this.plugin.getAuthHandler().playerQuit(player.getUniqueId());
    }
}
