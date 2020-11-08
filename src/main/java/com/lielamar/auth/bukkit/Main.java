package com.lielamar.auth.bukkit;

import com.lielamar.auth.bukkit.commands.CommandHandler;
import com.lielamar.auth.bukkit.handlers.AuthHandler;
import com.lielamar.auth.bukkit.handlers.ConfigHandler;
import com.lielamar.auth.bukkit.handlers.MessageHandler;
import com.lielamar.auth.bukkit.listeners.BungeeMessageListener;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.auth.bungee.BungeeMessagingUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private BungeeMessageListener pluginMessageListener;
    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private AuthHandler authHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.setupAuth();
        this.registerListeners();

        Bukkit.getOnlinePlayers().forEach(pl -> this.authHandler.playerJoin(pl.getUniqueId()));
    }

    public void setupAuth() {
        this.messageHandler = new MessageHandler(this);
        this.configHandler = new ConfigHandler(this);
        this.authHandler = new AuthHandler(this);

        new CommandHandler(this);
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnPlayerConnection(this), this);
        pm.registerEvents(new DisabledEvents(this), this);

        this.pluginMessageListener = new BungeeMessageListener(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessagingUtils.channelName);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, BungeeMessagingUtils.channelName, pluginMessageListener);
    }

    public BungeeMessageListener getPluginMessageListener() { return this.pluginMessageListener; }
    public MessageHandler getMessageHandler() { return this.messageHandler; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
}