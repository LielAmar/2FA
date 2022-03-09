package com.lielamar.auth.bukkit.communication;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BasicAuthCommunication extends AuthCommunicationHandler {

    private final TwoFactorAuthentication plugin;

    public BasicAuthCommunication(TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        long timeout = this.plugin.getConfigHandler().getCommunicationTimeout();

        // Timeouts all callbacks that were set more than ${timeout} seconds ago using #onTimeout
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long currentTimestamp = System.currentTimeMillis();

            super.callbacks.entrySet().removeIf(entry -> {
                if(((currentTimestamp - entry.getValue().getExecutionStamp()) / 1000) > (timeout / 20)) {
                    entry.getValue().onTimeout();
                    return true;
                }
                return false;
            });
        }, timeout, timeout);
    }


    @Override
    public void loadPlayerState(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback) {
        UUID callbackUUID = registerCallback(callback);

        AuthHandler.AuthState authState = this.plugin.getAuthHandler().getAuthState(uuid);
        if(authState == null) authState = AuthHandler.AuthState.NONE;

        super.onResponse(uuid, callbackUUID, MessageType.LOAD_STATE, authState);
    }

    @Override
    public void setPlayerState(@NotNull UUID uuid, AuthHandler.@NotNull AuthState authState, @Nullable AuthCommunicationCallback callback) {
        UUID callbackUUID = registerCallback(callback);

        this.plugin.getAuthHandler().changeState(uuid, authState);

        super.onResponse(uuid, callbackUUID, MessageType.SET_STATE, authState);
    }

    @Override
    public void checkCommunication(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback) {
        UUID callbackUUID = registerCallback(callback);

        super.onResponse(uuid, callbackUUID, MessageType.CHECK_COMMUNICATION, AuthHandler.AuthState.NONE);
    }
}
