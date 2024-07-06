package com.lielamar.auth.api.events;

import com.lielamar.auth.api.auth.AuthState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayerStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractPlayerStateChangeEvent abstractEvent;

    public BukkitPlayerStateChangeEvent(@NotNull Player player, @Nullable AuthState oldAuthState, @NotNull AuthState newAuthState) {
        this.abstractEvent = new AbstractPlayerStateChangeEvent(player, oldAuthState, newAuthState) {
            @Override
            public boolean isPlatformCancelled() {
                return BukkitPlayerStateChangeEvent.this.isCancelled();
            }

            @Override
            public void setPlatformCancelled(boolean cancelled) {
                BukkitPlayerStateChangeEvent.this.setCancelled(cancelled);
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

    public @NotNull Player getPlayer() {
        return (Player) abstractEvent.getPlayer();
    }

    public @Nullable AuthState getOldAuthState() {
        return abstractEvent.getOldAuthState();
    }

    public void setOldAuthState(@Nullable AuthState oldAuthState) {
        abstractEvent.setOldAuthState(oldAuthState);
    }

    public @NotNull AuthState getNewAuthState() {
        return abstractEvent.getNewAuthState();
    }

    public void setNewAuthState(@NotNull AuthState newAuthState) {
        abstractEvent.setNewAuthState(newAuthState);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
