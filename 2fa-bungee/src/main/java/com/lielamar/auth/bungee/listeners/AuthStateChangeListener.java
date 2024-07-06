package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.api.events.BungeePlayerStateChangeEvent;
import com.lielamar.auth.api.auth.AuthState;
import com.lielamar.auth.api.stats.StatsTracker;
import jakarta.inject.Inject;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class AuthStateChangeListener implements Listener {
    private final StatsTracker authTracker;

    @Inject
    public AuthStateChangeListener(final @NotNull StatsTracker authTracker) {
        this.authTracker = authTracker;
    }

    @EventHandler
    public void onStateChange(final BungeePlayerStateChangeEvent event) {
        if (event.getNewAuthState().equals(AuthState.AUTHENTICATED)) {
            authTracker.incrementAuthentications();
        }
    }
}
