package com.lielamar.auth.velocity;

import com.google.inject.Inject;
import com.lielamar.auth.shared.TwoFactorAuthenticationPlugin;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.velocity.handlers.AuthHandler;
import com.lielamar.auth.velocity.handlers.ConfigHandler;
import com.lielamar.auth.velocity.handlers.MessageHandler;
import com.lielamar.auth.velocity.listeners.DisabledEvents;
import com.lielamar.auth.velocity.listeners.OnPluginMessage;
import com.lielamar.auth.velocity.listeners.OnVelocityPlayerConnections;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "twofa", name = "2FA", version = "1.7.1", description = "Add another layer of protection to your server", authors = {"Liel Amar", "SadGhost", "Wolfity"})
public class TwoFactorAuthentication implements TwoFactorAuthenticationPlugin {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    private ConfigHandler configHandler;
    private MessageHandler messageHandler;
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
        this.registerListeners();
    }

    @Override
    public void setupAuth() {
        this.configHandler = new ConfigHandler(this);
        this.messageHandler = new MessageHandler(this);
        this.authHandler = new AuthHandler(this);
    }

    public void registerListeners() {
        EventManager eventManager = this.proxy.getEventManager();
        eventManager.register(this, new OnPluginMessage(this));
        eventManager.register(this, new OnVelocityPlayerConnections(this));
        eventManager.register(this, new DisabledEvents(this));

        this.proxy.getChannelRegistrar().register(INCOMING = MinecraftChannelIdentifier.create(PluginMessagingHandler.channelName.split(":")[0], PluginMessagingHandler.channelName.split(":")[1]));
        this.proxy.getChannelRegistrar().register(OUTGOING = MinecraftChannelIdentifier.create(PluginMessagingHandler.channelName.split(":")[0], PluginMessagingHandler.channelName.split(":")[1]));
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public Path getDataDirectory() {
        return this.dataDirectory;
    }

    public MinecraftChannelIdentifier getINCOMING() {
        return this.INCOMING;
    }

    public MinecraftChannelIdentifier getOUTGOING() {
        return this.OUTGOING;
    }

    @Override
    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public AuthHandler getAuthHandler() {
        return this.authHandler;
    }
}
