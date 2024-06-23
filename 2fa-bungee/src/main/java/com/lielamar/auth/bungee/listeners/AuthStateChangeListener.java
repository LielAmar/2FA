package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.api.events.BungeePlayerStateChangeEvent;
import com.lielamar.auth.core.handlers.AbstractAuthHandler;
import com.lielamar.auth.core.utils.AuthTracker;
import jakarta.inject.Inject;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class AuthStateChangeListener implements Listener {
    private final AuthTracker authTracker;

    @Inject
    public AuthStateChangeListener(final @NotNull AuthTracker authTracker) {
        this.authTracker = authTracker;
    }

    @EventHandler
    public void onStateChange(final BungeePlayerStateChangeEvent event) {
        if (event.getNewAuthState().equals(AbstractAuthHandler.AuthState.AUTHENTICATED)) {
            authTracker.incrementAuths();
        }
    }
}
