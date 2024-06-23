package com.lielamar.auth.bungee.listeners;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
public final class ListenerRegistererImpl implements IListenerRegisterer {
    @NotNull private final PluginManager pluginManager;
    @NotNull private final Plugin plugin;
    @NotNull private final List<Listener> listeners;

    @Inject
    public ListenerRegistererImpl(final @NotNull PluginManager pluginManager,
                                  final @NotNull Plugin plugin,
                                  final @NotNull List<Listener> listeners) {
        this.pluginManager = pluginManager;
        this.plugin = plugin;
        this.listeners = listeners;
    }

    @Override
    public void registerListeners() {
        listeners.forEach((listener) -> pluginManager.registerListener(plugin, listener));
    }
}
