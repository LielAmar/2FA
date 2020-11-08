package com.lielamar.auth.bukkit.events;

import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStateChangeEvent extends Event implements Cancellable {

    /**
     * This class was originally written by Connor Linfoot (https://github.com/ConnorLinfoot/MC2FA).
     */

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled = false;
    private AuthHandler.AuthState authState;

    public PlayerStateChangeEvent(Player player, AuthHandler.AuthState authState) {
        this.player = player;
        this.authState = authState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public AuthHandler.AuthState getAuthState() {
        return authState;
    }

    public void setAuthState(AuthHandler.AuthState authState) {
        this.authState = authState;
    }
}