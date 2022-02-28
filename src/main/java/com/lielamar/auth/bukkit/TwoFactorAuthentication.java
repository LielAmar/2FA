package com.lielamar.auth.bukkit;

import com.lielamar.auth.bukkit.commands.TwoFactorAuthenticationCommand;
import com.lielamar.auth.shared.TwoFactorAuthenticationPlugin;
import com.lielamar.lielsutils.bukkit.updater.SpigotUpdateChecker;
import com.lielamar.lielsutils.bukkit.bstats.BukkitMetrics;
import com.lielamar.lielsutils.bukkit.files.FileManager;

import com.lielamar.auth.bukkit.handlers.*;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.utils.AuthTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TwoFactorAuthentication extends JavaPlugin implements TwoFactorAuthenticationPlugin {

    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private StorageHandler storageHandler;
    private AuthHandler authHandler;
    private AuthTracker authTracker;

    @Override
    public void onEnable() {
        this.setupDependencies();

        if(!Bukkit.getPluginManager().isPluginEnabled(this))
            return;

        this.sendStartupMessage();

        this.setupAuth();
        this.registerListeners();
        this.registerCommands();

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
        FileManager fileManager = new FileManager(this);

        this.messageHandler = new MessageHandler(fileManager);
        this.configHandler = new ConfigHandler(fileManager);
        this.storageHandler = StorageHandler.loadStorageHandler(this.configHandler, getDataFolder().getAbsolutePath());

        this.authHandler = new AuthHandler(this);
        this.authTracker = new AuthTracker();

//        pluginMessageListener = new BungeecordMessageHandler(this);
//        getServer().getMessenger().registerOutgoingPluginChannel(this, PluginMessagingHandler.channelName);
//        getServer().getMessenger().registerIncomingPluginChannel(this, PluginMessagingHandler.channelName, pluginMessageListener);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnAuthStateChange(this), this);
        pm.registerEvents(new OnPlayerConnection(this), this);
        pm.registerEvents(new DisabledEvents(this), this);
    }

    private void registerCommands() {
        new TwoFactorAuthenticationCommand(this).registerCommand(this);
    }


    private void setupBStats() {
        int pluginId = 9355;
        BukkitMetrics metrics = new BukkitMetrics(this, pluginId);

        metrics.addCustomChart(new BukkitMetrics.SingleLineChart("authentications", () -> {
            int value = this.authTracker.getAuthentications();
            this.authTracker.setAuthentications(0);
            return value;
        }));
    }

    private void setupUpdateChecker() {
        if(this.configHandler.shouldCheckForUpdates())
            new SpigotUpdateChecker(this, 85594).checkForUpdates();
    }


    public MessageHandler getMessageHandler() { return this.messageHandler; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public StorageHandler getStorageHandler() { return this.storageHandler; }
    public AuthHandler getAuthHandler() { return this.authHandler; }
    public AuthTracker getAuthTracker() { return this.authTracker; }
}