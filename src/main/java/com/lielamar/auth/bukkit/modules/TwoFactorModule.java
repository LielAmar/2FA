package com.lielamar.auth.bukkit.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.ConfigHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.lielsutils.bukkit.files.FileManager;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;

public class TwoFactorModule extends AbstractModule {
    private final TwoFactorAuthentication plugin;

    public TwoFactorModule(TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @Provides
    public TwoFactorAuthentication providePlugin() {
        return plugin;
    }

    @Provides
    public Server provideServer(JavaPlugin plugin) {
        return plugin.getServer();
    }

    @Provides
    @Singleton
    public FileManager provideFileManager(JavaPlugin plugin) {
        return new FileManager(plugin);
    }

    @Provides
    @Singleton
    public AuthHandler provideAuthHandler(com.lielamar.auth.bukkit.handlers.AuthHandler authHandler) {
        return authHandler;
    }

    @Provides
    @Singleton
    public MessageHandler provideMessageHandler(com.lielamar.auth.bukkit.handlers.MessageHandler messageHandler) {
        return messageHandler;
    }

    @Provides
    @Singleton
    public ConfigHandler provideConfigHandler(com.lielamar.auth.bukkit.handlers.ConfigHandler configHandler) {
        return configHandler;
    }

    @Provides
    @Singleton
    public StorageHandler provideStorageHandler(ConfigHandler configHandler, JavaPlugin javaPlugin) {
        return StorageHandler.loadStorageHandler(configHandler, javaPlugin.getDataFolder().getAbsolutePath());
    }
}
