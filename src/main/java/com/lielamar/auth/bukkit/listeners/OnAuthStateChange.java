package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnAuthStateChange implements Listener {

    private final TwoFactorAuthentication main;

    public OnAuthStateChange(TwoFactorAuthentication main) {
        this.main = main;
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getNewAuthState().equals(AuthHandler.AuthState.AUTHENTICATED)) {
            event.getPlayer().setFlySpeed((float) 0.1);
            event.getPlayer().setWalkSpeed((float) 0.2);
        }
    }

    @EventHandler
    public void onPreAuthPostAuth(PlayerStateChangeEvent event) {
        Player player = event.getPlayer();

        if(player == null || !player.isOnline())
            return;

        if(event.getNewAuthState() == AuthHandler.AuthState.PENDING_LOGIN) {
            if(main.getConfigHandler().shouldTeleportBeforeAuth() && main.getConfigHandler().teleportBeforeAuthLocation() != null)
                player.teleport(main.getConfigHandler().teleportBeforeAuthLocation());
        }

        if(event.getNewAuthState() == AuthHandler.AuthState.AUTHENTICATED || event.getNewAuthState() == AuthHandler.AuthState.DISABLED) {
            if(main.getConfigHandler().shouldTeleportAfterAuth() && main.getConfigHandler().teleportAfterAuthLocation() != null)
                player.teleport(main.getConfigHandler().teleportAfterAuthLocation());
        }
    }
}
