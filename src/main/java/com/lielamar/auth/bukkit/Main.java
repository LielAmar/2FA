package com.lielamar.auth.bukkit;

import com.lielamar.auth.bukkit.commands.CommandHandler;
import com.lielamar.auth.bukkit.handlers.AuthHandler;
import com.lielamar.auth.bukkit.handlers.BungeecordMessageHandler;
import com.lielamar.auth.bukkit.handlers.ConfigHandler;
import com.lielamar.auth.bukkit.handlers.MessageHandler;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.auth.shared.Metrics;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private BungeecordMessageHandler pluginMessageListener;
    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.setupAuth();
        this.setupBStats();
        this.registerListeners();

        Bukkit.getOnlinePlayers().forEach(pl -> this.authHandler.playerJoin(pl.getUniqueId()));
    }

    public void setupAuth() {
        this.messageHandler = new MessageHandler(this);
        this.configHandler = new ConfigHandler(this);
        this.authHandler = new AuthHandler(this);

        if(commandHandler == null)
            this.commandHandler = new CommandHandler(this);
    }

    public void setupBStats() {
        int pluginId = 9355;
        new Metrics(this, pluginId);
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnAuthStateChange(), this);
        pm.registerEvents(new OnPlayerConnection(this), this);
        pm.registerEvents(new DisabledEvents(this), this);

        pluginMessageListener = new BungeecordMessageHandler(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.channelName);
        getServer().getMessenger().registerIncomingPluginChannel(this, Constants.channelName, pluginMessageListener);
    }

    public BungeecordMessageHandler getPluginMessageListener() { return this.pluginMessageListener; }
    public MessageHandler getMessageHandler() { return this.messageHandler; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
}