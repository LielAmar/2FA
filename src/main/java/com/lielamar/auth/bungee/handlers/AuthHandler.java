package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.bungee.events.PlayerStateChangeEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    @Override
    public void changeState(UUID uuid, AuthState authState) {
        if(authState == getAuthState(uuid))
            return;

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if(player != null) {
            PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, authState);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
            if(event.isCancelled())
                return;

            authState = event.getAuthState();
        }

        authStates.put(uuid, authState);
    }
}