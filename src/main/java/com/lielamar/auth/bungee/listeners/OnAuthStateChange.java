package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.TwoFactorAuthentication;
import com.lielamar.auth.bungee.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnAuthStateChange implements Listener {

    private final TwoFactorAuthentication main;

    public OnAuthStateChange(TwoFactorAuthentication main) {
        this.main = main;
    }


    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getNewAuthState().equals(AuthHandler.AuthState.AUTHENTICATED))
            this.main.getAuthTracker().setAuthentications(this.main.getAuthTracker().getAuthentications() + 1);
    }
}
