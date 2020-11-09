package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnAuthStateChange implements Listener {

    @EventHandler
    public void onAuthState(PlayerStateChangeEvent event) {
        if(event.getAuthState().equals(AuthHandler.AuthState.AUTHENTICATED)) {
            event.getPlayer().setFlySpeed((float) 0.1);
            event.getPlayer().setWalkSpeed((float) 0.2);
        }
    }
}
