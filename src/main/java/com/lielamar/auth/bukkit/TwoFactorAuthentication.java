package com.lielamar.auth.bukkit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.lielamar.auth.bukkit.commands.TwoFactorAuthenticationCommand;
import com.lielamar.auth.bukkit.communication.BasicAuthCommunication;
import com.lielamar.auth.bukkit.communication.ProxyAuthCommunication;
import com.lielamar.auth.bukkit.listeners.OnMapDrop;
import com.lielamar.auth.bukkit.modules.TwoFactorModule;
import com.lielamar.auth.shared.TwoFactorAuthenticationPlugin;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.communication.CommunicationMethod;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.updater.SpigotUpdateChecker;
import com.lielamar.lielsutils.bukkit.bstats.BukkitMetrics;
import com.lielamar.lielsutils.bukkit.files.FileManager;

import com.lielamar.auth.bukkit.handlers.*;
import com.lielamar.auth.bukkit.listeners.DisabledEvents;
import com.lielamar.auth.bukkit.listeners.OnAuthStateChange;
import com.lielamar.auth.bukkit.listeners.OnPlayerConnection;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.utils.AuthTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.apache.logging.log4j.core.Logger;

import java.util.Iterator;

public class TwoFactorAuthentication extends JavaPlugin implements TwoFactorAuthenticationPlugin {

    private Injector injector;

    private ConfigHandler configHandler;
    private StorageHandler storageHandler;
    private AuthHandler authHandler;
    private AuthTracker authTracker;

    @Override
    public void onEnable() {
        // Set the injector for the dependency injection
        injector = Guice.createInjector(Stage.PRODUCTION, new TwoFactorModule(this));

        this.setupDependencies();

        if (!this.getServer().getPluginManager().isPluginEnabled(this)) {
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
            Class.forName("com.warrenstrange.googleauth.GoogleAuthenticator");
        } catch (ClassNotFoundException exception) {
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[2FA] The default spigot dependency loader either does not exist or failed to load dependencies. Falling back to a custom dependency loader");
            new DependencyHandler(this);
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TwoFactorAuthenticationPlaceholders(this).register();
        }
    }

    @Override
    public com.lielamar.auth.shared.handlers.AuthHandler setupAuth() {
        this.configHandler = injector.getInstance(ConfigHandler.class);
        this.configHandler = injector.getInstance(ConfigHandler.class);
        AuthCommunicationHandler authCommunicationHandler;

        if (this.configHandler.getCommunicationMethod() == CommunicationMethod.PROXY) {
            authCommunicationHandler = new ProxyAuthCommunication(this);

            getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.PROXY_CHANNEL_NAME);
            getServer().getMessenger().registerIncomingPluginChannel(this, Constants.PROXY_CHANNEL_NAME,
                    (PluginMessageListener) authCommunicationHandler);
        } else {
            authCommunicationHandler = new BasicAuthCommunication(this);
        }

        this.authHandler = new AuthHandler(this, storageHandler, authCommunicationHandler, new BasicAuthCommunication(this), getServer());
        return authHandler;
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

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

    private void sendStartupMessage() {
        String version = ChatColor.DARK_AQUA + "2FA " + ChatColor.AQUA + "v" + getDescription().getVersion();
        String madeBy = ChatColor.DARK_GRAY + "Made with " + ChatColor.RED + "love" + ChatColor.DARK_GRAY + " by "
                + ChatColor.AQUA + getDescription().getAuthors().toString().replaceAll("\\[", "").replace("]", "");
        String timeSpent = ChatColor.DARK_GRAY + "Time contributed so far " + ChatColor.AQUA + "~240 Hours";

        getServer().getConsoleSender().sendMessage("");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " ___    _______    ___    ");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "|__ \\  |   ____|  /   \\    ");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "   ) | |  |__    /  ^  \\   " + "    " + version);
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "  / /  |   __|  /  /_\\  \\  " + "    " + madeBy);
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " / /_  |  |    /  _____  \\ " + "    " + timeSpent);
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "|____| |__|   /__/     \\__\\");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "");
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
