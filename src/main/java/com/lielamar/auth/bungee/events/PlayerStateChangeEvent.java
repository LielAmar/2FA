package com.lielamar.auth.bungee.events;

import com.lielamar.auth.shared.handlers.AuthHandler;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStateChangeEvent extends Event implements Cancellable {

    private final ProxiedPlayer player;
    private boolean cancelled = false;
    private AuthHandler.AuthState oldAuthState, newAuthState;

    public PlayerStateChangeEvent(@NotNull ProxiedPlayer player, @Nullable AuthHandler.AuthState oldAuthState, @NotNull AuthHandler.AuthState newAuthState) {
        this.player = player;
        this.oldAuthState = oldAuthState;
        this.newAuthState = newAuthState;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public @Nullable AuthHandler.AuthState getOldAuthState() {
        return oldAuthState;
    }

    public void setOldAuthState(@NotNull AuthHandler.AuthState oldAuthState) {
        this.oldAuthState = oldAuthState;
    }

    public @NotNull AuthHandler.AuthState getNewAuthState() {
        return newAuthState;
    }

    public void setNewAuthState(@NotNull AuthHandler.AuthState newAuthState) {
        this.newAuthState = newAuthState;
    }
}