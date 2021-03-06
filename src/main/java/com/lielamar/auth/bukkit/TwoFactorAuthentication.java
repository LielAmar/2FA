package com.lielamar.auth.bukkit;

import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.lielsutils.bstats.MetricsSpigot;
import com.lielamar.auth.bukkit.commands.CommandHandler;
import com.lielamar.auth.bukkit.handlers.AuthHandler;
import com.lielamar.auth.bukkit.handlers.BungeecordMessageHandler;
import com.lielamar.auth.bukkit.handlers.ConfigHandler;
import com.lielamar.auth.bukkit.handlers.MessageHandler;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.lielsutils.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TwoFactorAuthentication extends JavaPlugin {

    private BungeecordMessageHandler pluginMessageListener;
    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private AuthHandler authHandler;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if(getConfig().getBoolean("Check For Updates"))
            new UpdateChecker(this, 85594).checkForUpdates();

        this.setupAuth();
        this.setupBStats();
        this.registerListeners();

        for(Player pl : Bukkit.getOnlinePlayers())
            this.authHandler.playerJoin(pl.getUniqueId());
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
        MetricsSpigot metrics = new MetricsSpigot(this, pluginId);

        metrics.addCustomChart(new MetricsSpigot.SingleLineChart("authentications", () -> {
            int value = authHandler.getAuthentications();
            authHandler.resetAuthentications();
            return value;
        }));
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnAuthStateChange(), this);
        pm.registerEvents(new OnPlayerConnection(this), this);
        pm.registerEvents(new DisabledEvents(this), this);

        pluginMessageListener = new BungeecordMessageHandler(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PluginMessagingHandler.channelName);
        getServer().getMessenger().registerIncomingPluginChannel(this, PluginMessagingHandler.channelName, pluginMessageListener);
    }

    public BungeecordMessageHandler getPluginMessageListener() { return this.pluginMessageListener; }
    public MessageHandler getMessageHandler() { return this.messageHandler; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
}