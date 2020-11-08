package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class BungeeCordAuthHandler extends AuthHandler {

    public BungeeCordAuthHandler(Main main) {
        super(main);
    }

    public void changeState(UUID uuid, AuthState authState) {
        if(getAuthState(uuid) == AuthHandler.AuthState.AUTHENTICATED) {
            main.getPluginMessageListener().setBungeeCordAuthenticated(uuid, true);
        }
    }

    @Override
    public void attemptAutoAuthentication(Player player) {
        // Sending a message to BungeeCord asking if the player is already authenticated.
        // * Handling the response in {@link BungeeMessageHandler}
        new BukkitRunnable() {
            @Override
            public void run() {
                main.getPluginMessageListener().isBungeeCordAuthenticated(player.getUniqueId());
            }
        }.runTaskLater(main, 1L);
    }
}
