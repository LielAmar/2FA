package com.lielamar.auth.api.events;

import com.lielamar.auth.spigot.handlers.AuthHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    private boolean cancelled = false;
    private AuthHandler.AuthState oldAuthState, newAuthState;

    public PlayerStateChangeEvent(@NotNull Player player, @Nullable AuthHandler.AuthState oldAuthState, @NotNull AuthHandler.AuthState newAuthState) {
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

    public @NotNull Player getPlayer() {
        return player;
    }

    public @Nullable AuthHandler.AuthState getOldAuthState() {
        return oldAuthState;
    }

    public void setOldAuthState(@Nullable AuthHandler.AuthState oldAuthState) {
        this.oldAuthState = oldAuthState;
    }

    public @NotNull AuthHandler.AuthState getNewAuthState() {
        return newAuthState;
    }

    public void setNewAuthState(@NotNull AuthHandler.AuthState newAuthState) {
        this.newAuthState = newAuthState;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}