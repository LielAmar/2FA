package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    @Override
    public void changeState(@NotNull UUID uuid, @NotNull AuthState authState) {
        if(authState == super.getAuthState(uuid))
            return;

        Player player = Bukkit.getPlayer(uuid);
        if(player != null) {
            PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, authStates.get(uuid), authState);

            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled())
                return;

            authState = event.getNewAuthState();
        }

        super.authStates.put(uuid, authState);
    }
}