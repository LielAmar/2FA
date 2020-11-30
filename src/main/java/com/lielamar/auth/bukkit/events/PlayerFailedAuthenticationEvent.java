package com.lielamar.auth.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerFailedAuthenticationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int failedAttempts;

    public PlayerFailedAuthenticationEvent(Player player, int failedAttempts) {
        this.player = player;
        this.failedAttempts = failedAttempts;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public int getFailedAttempts() {
        return this.failedAttempts;
    }
}