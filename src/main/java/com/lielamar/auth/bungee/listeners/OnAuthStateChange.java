package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.TwoFactorAuthentication;
import com.lielamar.auth.bungee.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class OnAuthStateChange implements Listener {

    private final TwoFactorAuthentication plugin;

    public OnAuthStateChange(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getNewAuthState().equals(AuthHandler.AuthState.AUTHENTICATED))
            this.plugin.getAuthTracker().setAuthentications(this.plugin.getAuthTracker().getAuthentications() + 1);
    }
}
