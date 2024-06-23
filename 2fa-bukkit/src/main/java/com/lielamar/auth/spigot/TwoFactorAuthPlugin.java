package com.lielamar.auth.spigot;

import com.lielamar.auth.api.ITwoFactorAuthPlugin;
import com.lielamar.auth.spigot.utils.ConsoleFilter;
import com.lielamar.auth.spigot.utils.Version;
import com.lielamar.auth.storage.StorageHandler;
import io.micronaut.context.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class TwoFactorAuthPlugin extends JavaPlugin implements ITwoFactorAuthPlugin {
    private ApplicationContext context;

    @Override
    public void onLoad() {
        this.context = ApplicationContext.builder()
                .classLoader(this.getClassLoader())
                .singletons(this)
                .build();
    }

    @Override
    public void onEnable() {
        context.start();

        if (!getServer().getPluginManager().isPluginEnabled(this)) {
            return;
        }

        if (!Version.getInstance().getServerVersion().above(Version.ServerVersion.v1_16_0)) {
            getLogger().warning("""
        Your server is running an outdated Minecraft version that support for might soon be discontinued.
        The plugin may not function as expected in these versions after discontinuation.
    """);
        }

        // Register console filter
        ((Logger) (LogManager.getRootLogger())).addFilter(new ConsoleFilter());
    }

    @Override
    public void onDisable() {
        removeConsoleFilter();

        StorageHandler storageHandler = context.getBean(StorageHandler.class);
        if (storageHandler != null) {
            storageHandler.unload();
        }
    }

    private void removeConsoleFilter() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        Iterator<Filter> filters = rootLogger.getFilters();

        while (filters.hasNext()) {
            Filter filter = filters.next();
            if (filter instanceof ConsoleFilter) {
                filter.stop();
            }
        }
    }


    @Override
    public void reloadPlugin() {

    }
}
