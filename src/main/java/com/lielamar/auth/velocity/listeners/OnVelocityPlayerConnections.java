package com.lielamar.auth.velocity.listeners;

import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;

public class OnVelocityPlayerConnections {

    private final TwoFactorAuthentication main;

    public OnVelocityPlayerConnections(TwoFactorAuthentication main) {
        this.main = main;
    }

    @Subscribe
    public void onJoin(LoginEvent event) {
        Player player = event.getPlayer();

        main.getAuthHandler().playerJoin(player.getUniqueId());
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();

        main.getAuthHandler().playerQuit(player.getUniqueId());
    }
}
