package com.lielamar.auth.shared;

import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.ConfigHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TwoFactorAuthenticationPlugin {

    void setupAuth();

    MessageHandler getMessageHandler();

    ConfigHandler getConfigHandler();

    AuthHandler getAuthHandler();

    default Logger getDependencyLogger() {
        return LoggerFactory.getLogger("2FALoader");
    }
}
