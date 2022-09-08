package com.lielamar.auth.bukkit.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.communication.BasicAuthCommunication;
import com.lielamar.auth.bukkit.communication.ProxyAuthCommunication;
import com.lielamar.auth.bukkit.handlers.ConfigHandler;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.communication.CommunicationMethod;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.utils.AuthTracker;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.Server;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.inject.Singleton;

public class AuthModule extends AbstractModule {

    @Provides
    @Singleton
    public AuthTracker provideAuthTracker() {
        return new AuthTracker();
    }

    @Provides
    @Singleton
    public AuthHandler provideAuthHandler(TwoFactorAuthentication plugin, ConfigHandler configHandler, StorageHandler storageHandler, Server server) {
        AuthCommunicationHandler authCommunicationHandler;

        if (configHandler.getCommunicationMethod() == CommunicationMethod.PROXY) {
            authCommunicationHandler = new ProxyAuthCommunication(plugin);

            server.getMessenger().registerOutgoingPluginChannel(plugin, Constants.PROXY_CHANNEL_NAME);
            server.getMessenger().registerIncomingPluginChannel(plugin, Constants.PROXY_CHANNEL_NAME,
                    (PluginMessageListener) authCommunicationHandler);
        } else {
            authCommunicationHandler = new BasicAuthCommunication(plugin);
        }

        return new com.lielamar.auth.bukkit.handlers.AuthHandler(plugin, storageHandler, authCommunicationHandler, new BasicAuthCommunication(plugin), server);
    }
}
