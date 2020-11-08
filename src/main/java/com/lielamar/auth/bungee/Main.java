package com.lielamar.auth.bungee;

import com.lielamar.auth.bungee.handlers.AuthHandler;
import com.lielamar.auth.bungee.listeners.OnBungeePlayerConnections;
import com.lielamar.auth.bungee.listeners.OnPluginMessage;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private AuthHandler authHandler;

    @Override
    public void onEnable() {
        setupAuth();

        getProxy().getPluginManager().registerListener(this, new OnPluginMessage(this));
        getProxy().getPluginManager().registerListener(this, new OnBungeePlayerConnections(this));

        getProxy().registerChannel(BungeeMessagingUtils.channelName);
    }

    public void setupAuth() {
        this.authHandler = new AuthHandler();
    }

    public AuthHandler getAuthHandler() { return this.authHandler; }
}
