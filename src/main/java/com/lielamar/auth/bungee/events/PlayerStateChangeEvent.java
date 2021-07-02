package com.lielamar.auth.bungee.events;

import com.lielamar.auth.shared.handlers.AuthHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class PlayerStateChangeEvent extends Event implements Cancellable {

    private final ProxiedPlayer player;
    private boolean cancelled = false;
    private AuthHandler.AuthState authState;

    public PlayerStateChangeEvent(ProxiedPlayer player, AuthHandler.AuthState authState) {
        this.player = player;
        this.authState = authState;
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

    public AuthHandler.AuthState getAuthState() {
        return authState;
    }

    public void setAuthState(AuthHandler.AuthState authState) {
        this.authState = authState;
    }
}
