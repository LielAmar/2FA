package com.lielamar.auth.core.communication;

import com.lielamar.auth.core.auth.AuthState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractCommunicationHandler {

    protected final Map<UUID, CommunicationCallback> callbacks;

    protected boolean connected;

    public AbstractCommunicationHandler() {
        this.callbacks = new HashMap<>();

        this.connected = false;
    }

    public void loadPlayerState(@NotNull UUID uuid) {
        this.loadPlayerState(uuid, null);
    }

    public void setPlayerState(@NotNull UUID uuid, @NotNull AuthState authState) {
        this.setPlayerState(uuid, authState, null);
    }

    public void checkCommunication(@NotNull UUID uuid) {
        this.checkCommunication(uuid, null);
    }

    public abstract void loadPlayerState(@NotNull UUID uuid, @Nullable CommunicationCallback callback);

    public abstract void setPlayerState(@NotNull UUID uuid, @NotNull AuthState authState, @Nullable CommunicationCallback callback);

    public abstract void checkCommunication(@NotNull UUID uuid, @Nullable CommunicationCallback callback);

    public void onResponse(@NotNull UUID playerUUID, @NotNull UUID messageUUID, @NotNull MessageType messageType,
            @NotNull AuthState authState) {
        CommunicationCallback callback = this.callbacks.get(messageUUID);

        if (callback != null) {
            callback.execute(authState);
        }

        this.callbacks.remove(messageUUID);

        if (messageType == MessageType.CHECK_COMMUNICATION) {
            this.connected = true;
        }
    }

    protected @NotNull UUID registerCallback(@Nullable CommunicationCallback callback) {
        UUID randomUUID = UUID.randomUUID();

        if (callback != null) {
            this.callbacks.put(randomUUID, callback);
        }

        return randomUUID;
    }


    public enum MessageType {
        LOAD_STATE,
        SET_STATE,
        CHECK_COMMUNICATION
    }
}
