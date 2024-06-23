package com.lielamar.auth.bungee;

import com.lielamar.auth.api.ITwoFactorAuthPlugin;
import com.lielamar.auth.bungee.listeners.DisabledEvents;
import com.lielamar.auth.core.utils.AuthTracker;
import io.micronaut.context.ApplicationContext;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class TwoFactorAuthPlugin extends Plugin implements ITwoFactorAuthPlugin {
    private ApplicationContext context;

    @Override
    public void onLoad() {
        context = ApplicationContext.builder()
                .classLoader(getClass().getClassLoader())
                .singletons(this, new AuthTracker())
                .build();
    }

    @Override
    public void onEnable() {
        this.setupAuth();
        this.registerListeners();

        this.setupBStats();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void setupAuth() {
    }

    public void registerListeners() {
        PluginManager pm = getProxy().getPluginManager();
        pm.registerListener(this, new OnPluginMessage(this));
        pm.registerListener(this, new OnBungeePlayerConnections(this));
        pm.registerListener(this, new DisabledEvents(this));
        pm.registerListener(this, new OnAuthStateChange(this));

        getProxy().registerChannel(PluginMessagingHandler.channelName);
    }

    @Override
    public void reloadPlugin() {

    }
}