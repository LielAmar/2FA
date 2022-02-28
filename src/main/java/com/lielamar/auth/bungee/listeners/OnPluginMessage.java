package com.lielamar.auth.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.bungee.TwoFactorAuthentication;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import com.lielamar.auth.shared.utils.Constants;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class OnPluginMessage implements Listener {

    private final TwoFactorAuthentication plugin;

    public OnPluginMessage(TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQueryReceive(PluginMessageEvent event) {
        // If the Channel name is not the 2FA's Channel name we want to return
        if(!event.getTag().equals(Constants.PROXY_CHANNEL_NAME))
            return;

        ByteArrayDataInput msg = ByteStreams.newDataInput(event.getData());
        String subChannel = msg.readUTF();

        // If the SubChannel name is the 2FA's SubChannel name
        if(subChannel.equals(Constants.PROXY_SUB_CHANNEL_NAME)) {
            UUID messageUUID = UUID.fromString(msg.readUTF());
            UUID playerUUID = UUID.fromString(msg.readUTF());

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);

            short bodyLength = msg.readShort();
            byte[] msgBody = new byte[bodyLength];
            msg.readFully(msgBody);

            DataInputStream msgBodyData = new DataInputStream(new ByteArrayInputStream(msgBody));
            try {
                AuthCommunicationHandler.MessageType messageType = AuthCommunicationHandler.MessageType.valueOf(msgBodyData.readUTF());

                if(messageType == AuthCommunicationHandler.MessageType.SET_STATE) {
                    AuthHandler.AuthState state = AuthHandler.AuthState.valueOf(msgBodyData.readUTF());

                    this.plugin.getAuthHandler().changeState(player.getUniqueId(), state);
                }

                sendResponse(messageUUID, player, messageType);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void sendResponse(UUID messageUUID, ProxiedPlayer player, AuthCommunicationHandler.MessageType messageType) {
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
        } catch(IOException exception) {
            exception.printStackTrace();
        }

        response.writeShort(msgBody.toByteArray().length);
        response.write(msgBody.toByteArray());

        player.getServer().getInfo().sendData(Constants.PROXY_CHANNEL_NAME, response.toByteArray());
    }
}
