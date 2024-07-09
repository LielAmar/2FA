package com.lielamar.auth.api.events.state;

import com.lielamar.auth.api.auth.AuthState;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BungeePlayerStateChangeEvent extends Event implements Cancellable {
    private final AbstractPlayerStateChangeEvent abstractEvent;

    public BungeePlayerStateChangeEvent(@NotNull ProxiedPlayer player, @Nullable AuthState oldAuthState, @NotNull AuthState newAuthState) {
        this.abstractEvent = new AbstractPlayerStateChangeEvent(player, oldAuthState, newAuthState) {
            @Override
            public boolean isPlatformCancelled() {
                return BungeePlayerStateChangeEvent.this.isCancelled();
            }

            @Override
            public void setPlatformCancelled(boolean cancelled) {
                BungeePlayerStateChangeEvent.this.setCancelled(cancelled);
            }
        };
    }

    @Override
    public boolean isCancelled() {
        return abstractEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        abstractEvent.setCancelled(cancelled);
    }

    public ProxiedPlayer getPlayer() {
        return (ProxiedPlayer) abstractEvent.getPlayer();
    }

    public @Nullable AuthState getOldAuthState() {
        return abstractEvent.getOldAuthState();
    }

    public void setOldAuthState(@NotNull AuthState oldAuthState) {
        abstractEvent.setOldAuthState(oldAuthState);
    }

    public @NotNull AuthState getNewAuthState() {
        return abstractEvent.getNewAuthState();
    }

    public void setNewAuthState(@NotNull AuthState newAuthState) {
        abstractEvent.setNewAuthState(newAuthState);
    }
}
