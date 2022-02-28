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
import org.jetbrains.annotations.NotNull;

public class OnAuthStateChange implements Listener {

    private final TwoFactorAuthentication plugin;
    private final Hash hash;

    public OnAuthStateChange(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        String hashType = this.plugin.getConfigHandler().getIpHashType().toUpperCase();
        if(hashType.equalsIgnoreCase("SHA256"))      this.hash = new SHA256();
        else if(hashType.equalsIgnoreCase("SHA512")) this.hash = new SHA512();
        else                                                    this.hash = new NoHash();
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getNewAuthState().equals(AuthHandler.AuthState.AUTHENTICATED)) {
            event.getPlayer().setFlySpeed((float) 0.1);
            event.getPlayer().setWalkSpeed((float) 0.2);

            this.plugin.getStorageHandler().setIP(event.getPlayer().getUniqueId(),
                    this.hash.hash(event.getPlayer().getAddress().getAddress().getHostAddress()));

            this.plugin.getAuthTracker().setAuthentications(this.plugin.getAuthTracker().getAuthentications() + 1);
        }
    }

    @EventHandler
    public void onPreAuthPostAuth(PlayerStateChangeEvent event) {
        Player player = event.getPlayer();

        if(!player.isOnline())
            return;

        if(event.getNewAuthState() == AuthHandler.AuthState.PENDING_LOGIN) {
            if(this.plugin.getConfigHandler().shouldTeleportBeforeAuth() && this.plugin.getConfigHandler().teleportBeforeAuthLocation() != null)
                player.teleport(this.plugin.getConfigHandler().teleportBeforeAuthLocation());
        }

        if(event.getNewAuthState() == AuthHandler.AuthState.AUTHENTICATED || event.getNewAuthState() == AuthHandler.AuthState.DISABLED) {
            if(this.plugin.getConfigHandler().shouldTeleportAfterAuth() && this.plugin.getConfigHandler().teleportAfterAuthLocation() != null)
                player.teleport(this.plugin.getConfigHandler().teleportAfterAuthLocation());
        }
    }
}
