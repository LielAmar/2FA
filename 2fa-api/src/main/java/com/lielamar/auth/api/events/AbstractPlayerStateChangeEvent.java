package com.lielamar.auth.api.events;

import com.lielamar.auth.core.handlers.AbstractAuthHandler.AuthState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPlayerStateChangeEvent {

    private final Object player; // Use Object to generalize Player types
    private boolean cancelled = false;
    private AuthState oldAuthState, newAuthState;

    public AbstractPlayerStateChangeEvent(@NotNull Object player, @Nullable AuthState oldAuthState, @NotNull AuthState newAuthState) {
        this.player = player;
        this.oldAuthState = oldAuthState;
        this.newAuthState = newAuthState;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Object getPlayer() {
        return player;
    }

    public @Nullable AuthState getOldAuthState() {
        return oldAuthState;
    }

    public void setOldAuthState(@NotNull AuthState oldAuthState) {
        this.oldAuthState = oldAuthState;
    }

    public @NotNull AuthState getNewAuthState() {
        return newAuthState;
    }

    public void setNewAuthState(@NotNull AuthState newAuthState) {
        this.newAuthState = newAuthState;
    }

    public abstract boolean isPlatformCancelled();

    public abstract void setPlatformCancelled(boolean cancelled);
}
