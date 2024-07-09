package com.lielamar.auth.spigot.listeners;

import com.lielamar.auth.api.communication.CommunicationMethod;
import com.lielamar.auth.core.listeners.IListenerRegisterer;
import com.lielamar.auth.spigot.handlers.ConfigHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
public final class ListenerRegistererImpl implements IListenerRegisterer {
    @NotNull private final ConfigHandler handler;
    @NotNull private final PluginManager pluginManager;
    @NotNull private final Plugin plugin;
    @NotNull private final List<Listener> listeners;

    @Inject
    public ListenerRegistererImpl(final @NotNull ConfigHandler handler,
                                  final @NotNull PluginManager pluginManager,
                                  final @NotNull Plugin plugin,
                                  final @NotNull List<Listener> listeners) {
        this.handler = handler;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
        this.listeners = listeners;
    }

    @Override
    public void registerListeners() {
        listeners.forEach((listener) -> {
            if (listener.getClass().equals(ProxyAuthDetection.class) &&
                    !handler.getCommunicationMethod().equals(CommunicationMethod.PROXY)) {
                // maybe add debug logging
                return;
            }

            pluginManager.registerEvents(listener, plugin);
        });
    }
}