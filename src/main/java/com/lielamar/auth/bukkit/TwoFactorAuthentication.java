package com.lielamar.auth.bukkit;

import com.lielamar.auth.bukkit.commands.TwoFactorAuthenticationCommand;
import com.lielamar.auth.bukkit.communication.BasicAuthCommunication;
import com.lielamar.auth.bukkit.communication.ProxyAuthCommunication;
import com.lielamar.auth.bukkit.handlers.*;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnMapDrop;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.auth.bukkit.utils.bstats.BukkitMetrics;
import com.lielamar.auth.bukkit.utils.file.FileManager;
import com.lielamar.auth.bukkit.utils.update.SpigotUpdateChecker;
import com.lielamar.auth.shared.TwoFactorAuthenticationPlugin;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.communication.CommunicationMethod;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.utils.AuthTracker;
import com.lielamar.auth.shared.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Iterator;

public class TwoFactorAuthentication extends JavaPlugin implements TwoFactorAuthenticationPlugin {

    private FileManager fileManager;
    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private StorageHandler storageHandler;
    private AuthHandler authHandler;
    private AuthTracker authTracker;

    @Override
    public void onEnable() {
        this.setupDependencies();

        if (!Bukkit.getPluginManager().isPluginEnabled(this)) {
            return;
        }

        // Register the ConsoleFilter
        ((Logger) (LogManager.getRootLogger())).addFilter(new ConsoleFilter());

        this.sendStartupMessage();

        this.setupAuth();
        this.authHandler.reloadOnlinePlayers();
        this.registerListeners();
        this.registerCommands();

        this.setupBStats();
        this.setupUpdateChecker();
    }

    @Override
    public void onDisable() {
        // Unregister the ConsoleFilter
        Iterator<Filter> filters = ((Logger) (LogManager.getRootLogger())).getFilters();
        
        while (filters.hasNext()) {
            Filter filter = filters.next();
            if (filter instanceof ConsoleFilter) {
                filter.stop();
            }
        }

        if (this.storageHandler != null) {
            this.storageHandler.unload();
        }
    }

    private void setupDependencies() {
        try {
            Class.forName("com.atlassian.onetime.service.DefaultTOTPService");
        } catch (ClassNotFoundException exception) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[2FA] The default spigot dependency loader either does not exist or failed to load dependencies. Falling back to a custom dependency loader");
            new DependencyHandler(this);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TwoFactorAuthenticationPlaceholders(this).register();
        }
    }

    private void sendStartupMessage() {
        String version = ChatColor.DARK_AQUA + "2FA " + ChatColor.AQUA + "v" + getDescription().getVersion();
        String madeBy = ChatColor.DARK_GRAY + "Made with " + ChatColor.RED + "love" + ChatColor.DARK_GRAY + " by "
                + ChatColor.AQUA + getDescription().getAuthors().toString().replaceAll("\\[", "").replace("]", "");
        String timeSpent = ChatColor.DARK_GRAY + "Time contributed so far " + ChatColor.AQUA + "~240 Hours";

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " ___    _______    ___    ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "|__ \\  |   ____|  /   \\    ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "   ) | |  |__    /  ^  \\   " + "    " + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "  / /  |   __|  /  /_\\  \\  " + "    " + madeBy);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " / /_  |  |    /  _____  \\ " + "    " + timeSpent);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "|____| |__|   /__/     \\__\\");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "");
    }

    @Override
    public void setupAuth() {
        // Registering the console filter
        this.fileManager = new FileManager(this);

        this.messageHandler = new MessageHandler(fileManager);
        this.configHandler = new ConfigHandler(fileManager);
        this.storageHandler = StorageHandler.loadStorageHandler(this.configHandler, getDataFolder().getAbsolutePath());

        AuthCommunicationHandler authCommunicationHandler;

        if (this.configHandler.getCommunicationMethod() == CommunicationMethod.PROXY) {
            authCommunicationHandler = new ProxyAuthCommunication(this);

            getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.PROXY_CHANNEL_NAME);
            getServer().getMessenger().registerIncomingPluginChannel(this, Constants.PROXY_CHANNEL_NAME,
                    (PluginMessageListener) authCommunicationHandler);
        } else {
            authCommunicationHandler = new BasicAuthCommunication(this);
        }

        this.authHandler = new AuthHandler(this, storageHandler, authCommunicationHandler, new BasicAuthCommunication(this));
        this.authTracker = new AuthTracker();
    }

    private void registerListeners() {
        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new OnAuthStateChange(this), this);
        pm.registerEvents(new OnPlayerConnection(this), this);
        pm.registerEvents(new DisabledEvents(this), this);
        pm.registerEvents(new OnMapDrop(this), this);
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
        if (this.configHandler.shouldCheckForUpdates()) {
            new SpigotUpdateChecker(this, 85594).checkForUpdates();
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    public StorageHandler getStorageHandler() {
        return this.storageHandler;
    }

    @Override
    public AuthHandler getAuthHandler() {
        return this.authHandler;
    }

    public AuthTracker getAuthTracker() {
        return this.authTracker;
    }
}
