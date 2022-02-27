package com.lielamar.auth.shared.handlers;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AuthCommunicationHandler {

    public abstract AuthHandler.AuthState loadPlayerState(@NotNull UUID uuid);
    public abstract AuthHandler.AuthState setPlayerState(@NotNull UUID uuid, @NotNull AuthHandler.AuthState authState);

}