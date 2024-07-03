package com.lielamar.auth.core.communication;

import com.lielamar.auth.core.auth.AuthState;

import java.util.UUID;

public interface CommunicationCallback {
    void execute(AuthState authState);

    void onTimeout();

    long getExecutionStamp();

    UUID getUUID();
}