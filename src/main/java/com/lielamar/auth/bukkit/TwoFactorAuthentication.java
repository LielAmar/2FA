package com.lielamar.auth.bukkit;

import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.lielsutils.bstats.MetricsSpigot;
import com.lielamar.auth.bukkit.commands.CommandHandler;
import com.lielamar.auth.bukkit.handlers.AuthHandler;
import com.lielamar.auth.bukkit.handlers.BungeecordMessageHandler;
import com.lielamar.auth.bukkit.handlers.ConfigHandler;
import com.lielamar.auth.bukkit.handlers.MessageHandler;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.lielsutils.files.FileManager;
import com.lielamar.lielsutils.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Properties;

public class TwoFactorAuthentication extends JavaPlugin {

    private BungeecordMessageHandler pluginMessageListener;

    private FileManager fileManager;

    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private StorageHandler storageHandler;
    private AuthHandler authHandler;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        // Loading dependencies
        try {
            Properties properties = new Properties();
            properties.load(TwoFactorAuthentication.class.getClassLoader().getResourceAsStream("project.properties"));

            new DependencyManager(this, properties);
        } catch (IOException exception) {
            System.out.println("[-] 2FA failed to load dependencies. The plugin might not support all features!" +
                    "\nThis has most likely happened because your server doesn't have access to the internet!");
        }

        this.setupAuth();
        this.registerListeners();
        this.setupBStats();
        this.setupUpdateChecker();

        // Applying 2fa for online players
        for(Player pl : Bukkit.getOnlinePlayers())
            this.authHandler.playerJoin(pl.getUniqueId());
    }


    public void setupAuth() {
        this.fileManager = new FileManager(this);
        this.messageHandler = new MessageHandler(fileManager);
        this.configHandler = new ConfigHandler(fileManager);
        this.storageHandler = StorageHandler.loadStorageHandler(this.configHandler, getDataFolder().getAbsolutePath());
        this.authHandler = new AuthHandler(this);
        this.commandHandler = new CommandHandler(this);
    }

    private void setupBStats() {
        int pluginId = 9355;
        MetricsSpigot metrics = new MetricsSpigot(this, pluginId);

        metrics.addCustomChart(new MetricsSpigot.SingleLineChart("authentications", () -> {
            int value = authHandler.getAuthentications();
            authHandler.resetAuthentications();
            return value;
        }));
    }

    private void setupUpdateChecker() {
        // Whether the plugin should check for updates
        if(this.configHandler.shouldCheckForUpdates())
            new UpdateChecker(this, 85594).checkForUpdates();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnAuthStateChange(), this);
        pm.registerEvents(new OnPlayerConnection(this), this);
        pm.registerEvents(new DisabledEvents(this), this);

        pluginMessageListener = new BungeecordMessageHandler(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PluginMessagingHandler.channelName);
        getServer().getMessenger().registerIncomingPluginChannel(this, PluginMessagingHandler.channelName, pluginMessageListener);
    }


    public BungeecordMessageHandler getPluginMessageListener() { return this.pluginMessageListener; }
    public FileManager getFileManager() { return this.fileManager; }
    public MessageHandler getMessageHandler() { return this.messageHandler; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public StorageHandler getStorageHandler() { return this.storageHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
    public CommandHandler getCommandHandler() { return this.commandHandler; }
}