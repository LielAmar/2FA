package com.lielamar.auth.bungee;

import com.lielamar.auth.bungee.handlers.AuthHandler;
import com.lielamar.auth.bungee.handlers.ConfigHandler;
import com.lielamar.auth.bungee.handlers.MessageHandler;
import com.lielamar.auth.bungee.listeners.DisabledEvents;
import com.lielamar.auth.bungee.listeners.OnBungeePlayerConnections;
import com.lielamar.auth.bungee.listeners.OnPluginMessage;
import com.lielamar.auth.shared.utils.Constants;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Main extends Plugin {

    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private AuthHandler authHandler;

    @Override
    public void onEnable() {
        this.setupAuth();
        this.setupListeners();
    }

    public void setupAuth() {
        this.messageHandler = new MessageHandler(this);
        this.configHandler = new ConfigHandler(this);
        this.authHandler = new AuthHandler();
    }

    public void setupListeners() {
        PluginManager pm = getProxy().getPluginManager();
        pm.registerListener(this, new OnPluginMessage(this));
        pm.registerListener(this, new OnBungeePlayerConnections(this));
        pm.registerListener(this, new DisabledEvents(this));

        getProxy().registerChannel(Constants.channelName);
    }

    public MessageHandler getMessageHandler() { return this.messageHandler; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
}