package com.lielamar.auth.shared.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AuthCommunicationHandler {

    protected final Map<UUID, AuthCommunicationCallback> callbacks;

    public AuthCommunicationHandler() {
        this.callbacks = new HashMap<>();
    }

    public void loadPlayerState(@NotNull UUID uuid) { this.loadPlayerState(uuid, null); }
    public void setPlayerState(@NotNull UUID uuid, @NotNull AuthHandler.AuthState authState) { this.setPlayerState(uuid, authState, null); }
    public void checkCommunication(@NotNull UUID uuid) { this.checkCommunication(uuid, null); }

    public abstract void loadPlayerState(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback);
    public abstract void setPlayerState(@NotNull UUID uuid, @NotNull AuthHandler.AuthState authState, @Nullable AuthCommunicationCallback callback);
    public abstract void checkCommunication(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback);


    public void onResponse(@NotNull UUID playerUUID, @NotNull UUID messageUUID,
                           @NotNull AuthHandler.AuthState authState) {
        AuthCommunicationCallback callback = this.callbacks.get(messageUUID);

        if(callback != null)
            callback.execute(playerUUID, authState);
    }

    protected @NotNull UUID registerCallback(@Nullable AuthCommunicationCallback callback) {
        UUID randomUUID = UUID.randomUUID();

        if(callback != null)
            this.callbacks.put(randomUUID, callback);

        return randomUUID;
    }


    public interface AuthCommunicationCallback {
        void execute(UUID playerUUID, AuthHandler.AuthState authState);
        long getExecutionStamp();
    }

    public enum MessageType {
        LOAD_STATE,
        SET_STATE,
        CHECK_COMMUNICATION
    }
}