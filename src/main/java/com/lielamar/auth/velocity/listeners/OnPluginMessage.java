package com.lielamar.auth.velocity.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class OnPluginMessage extends PluginMessagingHandler {

    private final TwoFactorAuthentication plugin;

    public OnPluginMessage(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onQueryReceive(PluginMessageEvent event) {
        if (event.getIdentifier() != this.plugin.getOUTGOING()) {
            return;
        }

        ByteArrayDataInput msg = ByteStreams.newDataInput(event.getData());
        String subChannel = msg.readUTF();

        // If the SubChannel name is the 2FA's SubChannel name
        if (subChannel.equals(Constants.PROXY_SUB_CHANNEL_NAME)) {
            UUID messageUUID = UUID.fromString(msg.readUTF());
            UUID playerUUID = UUID.fromString(msg.readUTF());

            Optional<Player> optionalPlayer = this.plugin.getProxy().getPlayer(playerUUID);

            if (!optionalPlayer.isPresent()) {
                return;
            }

            Player player = optionalPlayer.get();

            short bodyLength = msg.readShort();
            byte[] msgBody = new byte[bodyLength];
            msg.readFully(msgBody);

            DataInputStream msgBodyData = new DataInputStream(new ByteArrayInputStream(msgBody));
            try {
                AuthCommunicationHandler.MessageType messageType = AuthCommunicationHandler.MessageType.valueOf(msgBodyData.readUTF());

                if (messageType == AuthCommunicationHandler.MessageType.SET_STATE) {
                    AuthHandler.AuthState state = AuthHandler.AuthState.valueOf(msgBodyData.readUTF());

                    this.plugin.getAuthHandler().changeState(player.getUniqueId(), state);
                }

                sendResponse(messageUUID, player, messageType);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void sendResponse(UUID messageUUID, Player player, AuthCommunicationHandler.MessageType messageType) {
        AuthHandler.AuthState authState = this.plugin.getAuthHandler().getAuthState(player.getUniqueId());

        ByteArrayDataOutput response = ByteStreams.newDataOutput();
        response.writeUTF(Constants.PROXY_SUB_CHANNEL_NAME);
        response.writeUTF(messageUUID.toString());
        response.writeUTF(player.getUniqueId().toString());

        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        DataOutputStream msgBodyData = new DataOutputStream(msgBody);
        try {
            msgBodyData.writeUTF(messageType.name());
            msgBodyData.writeUTF(authState.name());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        response.writeShort(msgBody.toByteArray().length);
        response.write(msgBody.toByteArray());

        Optional<ServerConnection> optionalServer = player.getCurrentServer();
        optionalServer.ifPresent(serverConnection -> serverConnection.sendPluginMessage(this.plugin.getOUTGOING(), response.toByteArray()));
    }
}
