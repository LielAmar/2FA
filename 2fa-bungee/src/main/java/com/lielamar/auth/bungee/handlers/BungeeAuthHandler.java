package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.api.events.BungeePlayerStateChangeEvent;
import com.lielamar.auth.core.handlers.AbstractAuthHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class BungeeAuthHandler extends AbstractAuthHandler {

    @Inject
    public BungeeAuthHandler() {}

    @Override
    public void changeState(@NotNull UUID uuid, @NotNull AuthState authState) {
        if (authState == super.getAuthState(uuid)) {
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            BungeePlayerStateChangeEvent event = new BungeePlayerStateChangeEvent(player, authStates.get(uuid), authState);

            ProxyServer.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            authState = event.getNewAuthState();
        }

        super.authStates.put(uuid, authState);
    }
}
