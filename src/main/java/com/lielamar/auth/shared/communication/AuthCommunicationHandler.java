package com.lielamar.auth.shared.communication;

import com.lielamar.auth.shared.handlers.AuthHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AuthCommunicationHandler {

    protected final Map<UUID, AuthCommunicationCallback> callbacks;

    protected boolean connected;

    public AuthCommunicationHandler() {
        this.callbacks = new HashMap<>();

        this.connected = false;
    }

    public void loadPlayerState(@NotNull UUID uuid) { this.loadPlayerState(uuid, null); }
    public void setPlayerState(@NotNull UUID uuid, @NotNull AuthHandler.AuthState authState) { this.setPlayerState(uuid, authState, null); }
    public void checkCommunication(@NotNull UUID uuid) { this.checkCommunication(uuid, null); }

    public abstract void loadPlayerState(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback);
    public abstract void setPlayerState(@NotNull UUID uuid, @NotNull AuthHandler.AuthState authState, @Nullable AuthCommunicationCallback callback);
    public abstract void checkCommunication(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback);


    public void onResponse(@NotNull UUID playerUUID, @NotNull UUID messageUUID, @NotNull MessageType messageType,
                           @NotNull AuthHandler.AuthState authState) {
        AuthCommunicationCallback callback = this.callbacks.get(messageUUID);

        if(callback != null)
            callback.execute(authState);

        this.callbacks.remove(messageUUID);

        if(messageType == MessageType.CHECK_COMMUNICATION)
            this.connected = true;
    }

    protected @NotNull UUID registerCallback(@Nullable AuthCommunicationCallback callback) {
        UUID randomUUID = UUID.randomUUID();

        if(callback != null)
            this.callbacks.put(randomUUID, callback);

        return randomUUID;
    }


    public interface AuthCommunicationCallback {
        void execute(AuthHandler.AuthState authState);
        void onTimeout();

        long getExecutionStamp();

        UUID getPlayerUUID();
    }

    public enum MessageType {
        LOAD_STATE,
        SET_STATE,
        CHECK_COMMUNICATION
    }
}