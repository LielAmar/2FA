package com.lielamar.auth.shared;

import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.ConfigHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;

public interface TwoFactorAuthenticationPlugin {

    void setupAuth();

    MessageHandler getMessageHandler();
    ConfigHandler getConfigHandler();
    AuthHandler getAuthHandler();
}
