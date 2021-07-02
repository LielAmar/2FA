package com.lielamar.auth.velocity.events;

import com.lielamar.auth.shared.handlers.AuthHandler;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;

public class PlayerStateChangeEvent implements ResultedEvent<PlayerStateChangeEvent.StateResult> {

    private final Player player;
    private StateResult stateResult = StateResult.allowed();
    private AuthHandler.AuthState authState;

    public PlayerStateChangeEvent(Player player, AuthHandler.AuthState authState) {
        this.player = player;
        this.authState = authState;
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

    @Override
    public StateResult getResult() {
        return this.stateResult;
    }

    @Override
    public void setResult(StateResult stateResult) {
        this.stateResult = stateResult;
    }

    public static class StateResult implements ResultedEvent.Result {
        private static final PlayerStateChangeEvent.StateResult DENIED = new PlayerStateChangeEvent.StateResult();

        @Override
        public boolean isAllowed() { return true; }

        public static PlayerStateChangeEvent.StateResult denied() {
            return DENIED;
        }

        public static PlayerStateChangeEvent.StateResult allowed() {
            return new PlayerStateChangeEvent.StateResult();
        }
    }
}
