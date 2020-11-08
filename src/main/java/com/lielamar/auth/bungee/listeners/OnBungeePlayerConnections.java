package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnBungeePlayerConnections implements Listener {

    private final Main main;
    public OnBungeePlayerConnections(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(LoginEvent event) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(event.getConnection().getUniqueId());
        if(main.getAuthHandler().containsPlayer(p))
            main.getAuthHandler().removePlayer(p);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        if(main.getAuthHandler().containsPlayer(p))
            main.getAuthHandler().removePlayer(p);
    }
}
