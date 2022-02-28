package com.lielamar.auth.velocity.handlers;

import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.lielamar.auth.velocity.events.PlayerStateChangeEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    private final TwoFactorAuthentication main;

    public AuthHandler(TwoFactorAuthentication main) {
        this.main = main;
    }

    @Override
    public void changeState(@NotNull UUID uuid, @NotNull AuthState authState) {
        if(authState == getAuthState(uuid))
            return;

        Optional<Player> optionalPlayer = main.getProxy().getPlayer(uuid);
        
        if(optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();

            PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, authState);
            main.getProxy().getEventManager().fire(event);

            if(event.getResult() == PlayerStateChangeEvent.StateResult.denied())
                return;

            authState = event.getAuthState();
        }

        authStates.put(uuid, authState);
    }
}