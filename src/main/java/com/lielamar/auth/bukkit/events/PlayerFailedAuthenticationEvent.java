package com.lielamar.auth.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerFailedAuthenticationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int failedAttempts;

    public PlayerFailedAuthenticationEvent(Player player, int failedAttempts) {
        this.player = player;
        this.failedAttempts = failedAttempts;
    }

    public Player getPlayer() {
        return player;
    }

    public int getFailedAttempts() {
        return this.failedAttempts;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
