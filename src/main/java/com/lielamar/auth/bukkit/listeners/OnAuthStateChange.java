package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.utils.hash.Hash;
import com.lielamar.auth.shared.utils.hash.NoHash;
import com.lielamar.auth.shared.utils.hash.SHA256;
import com.lielamar.auth.shared.utils.hash.SHA512;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnAuthStateChange implements Listener {

    private final TwoFactorAuthentication main;
    private final Hash hash;

    public OnAuthStateChange(TwoFactorAuthentication main) {
        this.main = main;

        String hashType = main.getConfigHandler().getIpHashType().toUpperCase();
        if(hashType.equalsIgnoreCase("SHA256"))
            this.hash = new SHA256();
        else if(hashType.equalsIgnoreCase("SHA512"))
            this.hash = new SHA512();
        else
            this.hash = new NoHash();
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getNewAuthState().equals(AuthHandler.AuthState.AUTHENTICATED)) {
            event.getPlayer().setFlySpeed((float) 0.1);
            event.getPlayer().setWalkSpeed((float) 0.2);

            this.main.getStorageHandler().setIP(event.getPlayer().getUniqueId(),
                    this.hash.hash(event.getPlayer().getAddress().getAddress().getHostAddress()));

            this.main.getAuthTracker().setAuthentications(this.main.getAuthTracker().getAuthentications() + 1);
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
