package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerConnection implements Listener {

    private final TwoFactorAuthentication main;
    public OnPlayerConnection(TwoFactorAuthentication main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.main.getAuthHandler().playerJoin(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        this.main.getAuthHandler().playerQuit(player.getUniqueId());
    }
}
