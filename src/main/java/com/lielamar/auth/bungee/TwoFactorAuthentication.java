package com.lielamar.auth.bungee;

import com.lielamar.auth.bungee.handlers.AuthHandler;
import com.lielamar.auth.bungee.handlers.ConfigHandler;
import com.lielamar.auth.bungee.handlers.MessageHandler;
import com.lielamar.auth.bungee.listeners.DisabledEvents;
import com.lielamar.auth.bungee.listeners.OnAuthStateChange;
import com.lielamar.auth.bungee.listeners.OnBungeePlayerConnections;
import com.lielamar.auth.bungee.listeners.OnPluginMessage;
import com.lielamar.auth.bungee.utils.BungeeMetrics;
import com.lielamar.auth.shared.TwoFactorAuthenticationPlugin;
import com.lielamar.auth.shared.handlers.DependencyHandler;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.shared.utils.AuthTracker;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class TwoFactorAuthentication extends Plugin implements TwoFactorAuthenticationPlugin {

    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private AuthTracker authTracker;

    @Override
    public void onEnable() {
        new DependencyHandler(this); // Setup dependencies

        this.setupAuth();
        this.registerListeners();

        this.setupBStats();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void setupAuth() {
        this.messageHandler = new MessageHandler(this);
        this.configHandler = new ConfigHandler(this);
        this.authHandler = new AuthHandler();

        this.authTracker = new AuthTracker();
    }

    public void registerListeners() {
        PluginManager pm = getProxy().getPluginManager();
        pm.registerListener(this, new OnPluginMessage(this));
        pm.registerListener(this, new OnBungeePlayerConnections(this));
        pm.registerListener(this, new DisabledEvents(this));
        pm.registerListener(this, new OnAuthStateChange(this));

        getProxy().registerChannel(PluginMessagingHandler.channelName);
    }

    private void setupBStats() {
        int pluginId = 9355;
        BungeeMetrics metrics = new BungeeMetrics(this, pluginId);

        metrics.addCustomChart(new BungeeMetrics.SingleLineChart("authentications", () -> {
            int value = this.authTracker.getAuthentications();
            this.authTracker.setAuthentications(0);
            return value;
        }));
    }

    @Override
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    @Override
    public AuthHandler getAuthHandler() {
        return this.authHandler;
    }

    public AuthTracker getAuthTracker() {
        return this.authTracker;
    }
}
