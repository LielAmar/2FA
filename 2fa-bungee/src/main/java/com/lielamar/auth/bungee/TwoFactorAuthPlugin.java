package com.lielamar.auth.bungee;

import com.lielamar.auth.api.ITwoFactorAuthPlugin;
import com.lielamar.auth.bungee.listeners.IListenerRegisterer;
import com.lielamar.auth.core.utils.AuthTracker;
import io.micronaut.context.ApplicationContext;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

public class TwoFactorAuthPlugin extends Plugin implements ITwoFactorAuthPlugin {
    private ApplicationContext applicationContext;

    @Override
    public void onLoad() {
        applicationContext = ApplicationContext.builder()
                .classLoader(getClass().getClassLoader())
                .singletons(this, new AuthTracker())
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
    }

    @Override
    public void reloadPlugin() {

    }
}