package com.lielamar.auth.listeners;

import com.lielamar.auth.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    private Main main;
    public OnPlayerJoin(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if(!player.hasPermission("2fa.use") || player.hasPermission("2fa.exempt")) return;

        String secretKey = this.main.getAuthDatabaseManager().getDatabase().getSecretKey(player.getUniqueId());
        if(secretKey == null) {
            if(player.hasPermission("2fa.demand")) {
                this.main.getAuthManager().lockPlayer(player);
                this.main.getAuthDatabaseManager().generateKey(player);
            }
        } else {
            this.main.getAuthManager().lockPlayer(player);
            this.main.getAuthDatabaseManager().demandCode(player);
        }
    }
}
