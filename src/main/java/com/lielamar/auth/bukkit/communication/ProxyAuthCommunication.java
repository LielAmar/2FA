package com.lielamar.auth.bukkit.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ProxyAuthCommunication extends AuthCommunicationHandler implements PluginMessageListener {

    private final TwoFactorAuthentication plugin;

    public ProxyAuthCommunication(TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        long timeout = this.plugin.getConfigHandler().getCommunicationTimeout();

        // Timeouts all callbacks that were set more than ${timeout} seconds ago using #onTimeout
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTimestamp = System.currentTimeMillis();

            super.callbacks.entrySet().removeIf(entry -> {
                if(((currentTimestamp - entry.getValue().getExecutionStamp()) / 1000) > (timeout / 20)) {
                    entry.getValue().onTimeout();
                    return true;
                }
                return false;
            });
        }, timeout, timeout);
    }


    @Override
    public void loadPlayerState(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback) {
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        this.setMessageHeader(msg, uuid, registerCallback(callback));

        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        this.setMessageBody(msgBody, AuthCommunicationHandler.MessageType.LOAD_STATE);
        this.applyMessageBody(msg, msgBody);

        this.sendMessage(uuid, msg);
    }

    @Override
    public void setPlayerState(@NotNull UUID uuid, AuthHandler.@NotNull AuthState authState, @Nullable AuthCommunicationCallback callback) {
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        this.setMessageHeader(msg, uuid, registerCallback(callback));

        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        this.setMessageBody(msgBody, AuthCommunicationHandler.MessageType.SET_STATE, authState.name());
        this.applyMessageBody(msg, msgBody);

        this.sendMessage(uuid, msg);
    }

    @Override
    public void checkCommunication(@NotNull UUID uuid, @Nullable AuthCommunicationCallback callback) {
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        this.setMessageHeader(msg, uuid, registerCallback(callback));

        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        this.setMessageBody(msgBody, AuthCommunicationHandler.MessageType.CHECK_COMMUNICATION);
        this.applyMessageBody(msg, msgBody);

        this.sendMessage(uuid, msg);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] data) {
        if(!channel.equals(Constants.PROXY_CHANNEL_NAME))
            return;

        ByteArrayDataInput response = ByteStreams.newDataInput(data);
        String subChannelName = response.readUTF();

        if(!subChannelName.equals(Constants.PROXY_SUB_CHANNEL_NAME))
            return;

        UUID messageUUID = UUID.fromString(response.readUTF());
        UUID playerUUID = UUID.fromString(response.readUTF());

        short bodyLength = response.readShort();
        byte[] msgBody = new byte[bodyLength];
        response.readFully(msgBody);

        DataInputStream msgBodyData = new DataInputStream(new ByteArrayInputStream(msgBody));

        try {
            MessageType messageType = MessageType.valueOf(msgBodyData.readUTF());
            AuthHandler.AuthState authState = AuthHandler.AuthState.valueOf(msgBodyData.readUTF());

            super.onResponse(playerUUID, messageUUID, messageType, authState);
        } catch (IOException | IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }


    private void setMessageHeader(@NotNull ByteArrayDataOutput msg, @NotNull UUID uuid, @NotNull UUID callbackUUID) {
        msg.writeUTF(Constants.PROXY_SUB_CHANNEL_NAME);
        msg.writeUTF(callbackUUID.toString());
        msg.writeUTF(uuid.toString());
    }

    public void setMessageBody(@NotNull ByteArrayOutputStream msgBody, @NotNull AuthCommunicationHandler.MessageType messageType, @NotNull String... parameters) {
        DataOutputStream bodyData = new DataOutputStream(msgBody);

        try {
            bodyData.writeUTF(messageType.name());

            for(String param : parameters)
                bodyData.writeUTF(param);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void applyMessageBody(ByteArrayDataOutput msg, ByteArrayOutputStream msgBody) {
        msg.writeShort(msgBody.toByteArray().length);
        msg.write(msgBody.toByteArray());
    }


    public void sendMessage(@NotNull UUID uuid, @NotNull ByteArrayDataOutput msg) {
        Player player = Bukkit.getPlayer(uuid);

        if(player != null && player.isOnline())
            player.sendPluginMessage(this.plugin, Constants.PROXY_CHANNEL_NAME, msg.toByteArray());
    }
}
