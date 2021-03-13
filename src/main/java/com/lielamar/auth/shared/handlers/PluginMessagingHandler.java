package com.lielamar.auth.shared.handlers;

public abstract class PluginMessagingHandler {

    public static final String channelName = "2fa:2fa";
    protected final String subChannelName = "ForwardToPlayer";

    protected enum MessageAction {
        GET_STATE,
        SET_STATE
    }
}
