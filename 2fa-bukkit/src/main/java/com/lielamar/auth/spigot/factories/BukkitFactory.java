package com.lielamar.auth.spigot.factories;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

@Factory
public class BukkitFactory {

    @Bean
    public Server server(final @NotNull Plugin plugin) {
        return plugin.getServer();
    }

    @Bean
    public PluginManager pluginManager(final @NotNull Server server) {
        return server.getPluginManager();
    }

    @Bean
    public BukkitScheduler bukkitScheduler(final @NotNull Server server) {
        return server.getScheduler();
    }

    @Bean
    public ServicesManager servicesManager(final @NotNull Server server) {
        return server.getServicesManager();
    }

    @Bean
    public @NotNull Messenger messenger(final @NotNull Server server) {
        return server.getMessenger();
    }
}
