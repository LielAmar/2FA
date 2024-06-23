package com.lielamar.auth.api;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class TwoFAProvider {

    private ITwoFactorAuthService api = null;

    public TwoFAProvider(final @NotNull Plugin plugin) {

        final Logger logger = plugin.getLogger();
        final Server server = plugin.getServer();

        if (!server.getPluginManager().isPluginEnabled("2FA")) {
            logger.warning("Could not hook into 2FA's Service. This may cause major errors in the plugin.");
            return;
        }

        final RegisteredServiceProvider<ITwoFactorAuthService> serviceProvider = server.getServicesManager().getRegistration(ITwoFactorAuthService.class);
        if (serviceProvider == null) {
            logger.warning("Could not hook into the 2FA Service. This may cause major errors in the plugin.");
            return;
        }

        this.api = serviceProvider.getProvider();
    }

    public ITwoFactorAuthService getApi() {
        return api;
    }
}
