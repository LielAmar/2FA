package com.lielamar.auth.velocity;

import com.google.inject.Inject;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.velocity.handlers.AuthHandler;
import com.lielamar.auth.velocity.listeners.DisabledEvents;
import com.lielamar.auth.velocity.listeners.OnBungeePlayerConnections;
import com.lielamar.auth.velocity.listeners.OnPluginMessage;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "twofa", name = "2FA Plugin by Liel Amar", version = "1.3.2",
        description = "I did it!", authors = {"Liel Amar"})
public class TwoFactorAuthentication {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

//    private MessageHandler messageHandler;
//    private ConfigHandler configHandler;
    private AuthHandler authHandler;

    private MinecraftChannelIdentifier INCOMING;
    private MinecraftChannelIdentifier OUTGOING;

    @Inject
    public TwoFactorAuthentication(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        this.setupAuth();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.setupListeners();
    }


    public void setupAuth() {
//        this.messageHandler = new MessageHandler(this);
//        this.configHandler = new ConfigHandler(this);
        this.authHandler = new AuthHandler(this);
    }

    public void setupListeners() {
        EventManager eventManager = this.proxy.getEventManager();
        eventManager.register(this, new OnPluginMessage(this));
        eventManager.register(this, new OnBungeePlayerConnections(this));
        eventManager.register(this, new DisabledEvents(this));

        this.proxy.getChannelRegistrar().register(INCOMING = MinecraftChannelIdentifier.create(PluginMessagingHandler.channelName, "proxy"));
        this.proxy.getChannelRegistrar().register(OUTGOING = MinecraftChannelIdentifier.create(PluginMessagingHandler.channelName, "server"));
    }

    public ProxyServer getProxy() { return this.proxy; }
    public Path getDataDirectory() { return this.dataDirectory; }
    public MinecraftChannelIdentifier getINCOMING() { return this.INCOMING; }
    public MinecraftChannelIdentifier getOUTGOING() { return this.OUTGOING; }

//    public MessageHandler getMessageHandler() { return this.messageHandler; }
//    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
}