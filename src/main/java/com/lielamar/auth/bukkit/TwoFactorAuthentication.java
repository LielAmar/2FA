package com.lielamar.auth.bukkit;

import com.lielamar.lielsutils.bukkit.updater.SpigotUpdateChecker;
import com.lielamar.lielsutils.bukkit.bstats.SpigotMetrics;
import com.lielamar.lielsutils.bukkit.files.FileManager;

import com.lielamar.auth.bukkit.commands.CommandHandler;
import com.lielamar.auth.bukkit.handlers.*;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.utils.AuthTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TwoFactorAuthentication extends JavaPlugin {

    private BungeecordMessageHandler pluginMessageListener;

    private FileManager fileManager;

    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private StorageHandler storageHandler;
    private AuthHandler authHandler;
    private CommandHandler commandHandler;
    private AuthTracker authTracker;

    @Override
    public void onEnable() {
        this.setupDependencies();

        if(!Bukkit.getPluginManager().isPluginEnabled(this))
            return;

        this.sendStartupMessage();

        this.setupAuth();
        this.registerListeners();
        this.setupBStats();
        this.setupUpdateChecker();
    }

    @Override
    public void onDisable() {
        if(this.storageHandler != null)
            this.storageHandler.unload();
    }


    private void setupDependencies() {
        try { Class.forName("com.warrenstrange.googleauth.GoogleAuthenticator"); }
        catch(ClassNotFoundException exception) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[2FA] The default spigot dependency loader either does not exist or failed to load dependencies. Falling back to a custom dependency loader");
            new DependencyHandler(this);
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new TwoFactorAuthenticationPlaceholders(this).register();
    }

    private void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "                   ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "  ___  ______      ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " |__ \\|  ____/\\    ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "    ) | |__ /  \\   " + "    "
                + ChatColor.DARK_AQUA + "2FA " + ChatColor.AQUA + "v" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "   / /|  __/ /\\ \\  " + "    "
                + ChatColor.DARK_GRAY + "Made with " + ChatColor.RED + "♥" + ChatColor.DARK_GRAY + " by "
                + ChatColor.AQUA + getDescription().getAuthors().toString().replaceAll("\\[", "").replace("]", ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "  / /_| | / ____ \\ " + "    "
                + ChatColor.DARK_GRAY + "Time contributed so far " + ChatColor.AQUA + "~175 Hours");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " |____|_|/_/    \\_\\");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "                   ");
    }


    public void setupAuth() {
        this.fileManager = new FileManager(this);
        this.messageHandler = new MessageHandler(fileManager);
        this.configHandler = new ConfigHandler(fileManager);
        this.storageHandler = StorageHandler.loadStorageHandler(this.configHandler, getDataFolder().getAbsolutePath());
        this.authHandler = new AuthHandler(this);
        this.commandHandler = new CommandHandler(this);
        this.authTracker = new AuthTracker();
    }

    private void setupBStats() {
        int pluginId = 9355;
        SpigotMetrics metrics = new SpigotMetrics(this, pluginId);

        metrics.addCustomChart(new SpigotMetrics.SingleLineChart("authentications", () -> {
            int value = this.authTracker.getAuthentications();
            this.authTracker.setAuthentications(0);
            return value;
        }));
    }

    private void setupUpdateChecker() {
        // Whether the plugin should check for updates
        if(this.configHandler.shouldCheckForUpdates())
            new SpigotUpdateChecker(this, 85594).checkForUpdates();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnAuthStateChange(this), this);
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
    public AuthTracker getAuthTracker() { return this.authTracker; }
}