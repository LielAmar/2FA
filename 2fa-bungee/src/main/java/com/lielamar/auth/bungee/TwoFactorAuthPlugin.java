package com.lielamar.auth.bungee;

import com.lielamar.auth.api.ITwoFactorAuthPlugin;
import com.lielamar.auth.core.listeners.IListenerRegisterer;
import com.lielamar.auth.core.utils.AuthTracker;
import io.micronaut.context.ApplicationContext;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;

public class TwoFactorAuthPlugin extends Plugin implements ITwoFactorAuthPlugin {
    private ApplicationContext applicationContext;

    @Override
    public void onLoad() {
        applicationContext = ApplicationContext.builder()
                .classLoader(this.getClass().getClassLoader())
                .singletons(this)
                .properties(
                        Map.ofEntries(
                                Map.entry("datafolder", this.getDataFolder().getAbsoluteFile())
                        ))
                .build();
    }


    @Override
    public void onEnable() {
        applicationContext.start();

        final IListenerRegisterer listenerRegisterer = applicationContext.getBean(IListenerRegisterer.class);
        listenerRegisterer.registerListeners();
    }

    @Override
    public void onDisable() {
        applicationContext.stop();
    }

    @Override
    public void reloadPlugin() {

    }
}