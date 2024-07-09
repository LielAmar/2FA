package com.lielamar.auth.spigot.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.api.communication.MessageType;
import com.lielamar.auth.core.utils.Constants;
import jakarta.inject.Inject;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class ProxyCommunicationHandler implements PluginMessageListener {
    private final Server server;
    private final Plugin plugin;

    private boolean connected;

    @Inject
    public ProxyCommunicationHandler(final @NotNull Server server,
                                     final @NotNull Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] data) {
        if (!channel.equals(Constants.PROXY_CHANNEL_NAME)) {
            return;
        }

        ByteArrayDataInput response = ByteStreams.newDataInput(data);
        String subChannelName = response.readUTF();

        if (!subChannelName.equals(Constants.PROXY_SUB_CHANNEL_NAME)) {
            return;
        }

        UUID messageUUID = UUID.fromString(response.readUTF());

        short bodyLength = response.readShort();
        byte[] msgBody = new byte[bodyLength];
        response.readFully(msgBody);

        DataInputStream msgBodyData = new DataInputStream(new ByteArrayInputStream(msgBody));

        try {
            MessageType messageType = MessageType.valueOf(msgBodyData.readUTF());

            switch (messageType) {
                case PROXY_SYNACK:
                    acknowledgeProxy();
                    break;
                case AUTH_STATE:
                    // Handle authentication state request
                    break;
                case PROXY_AUTHENTICATE:
                    // Handle authentication request
                    break;
                default:
                    // Unknown message type
                    break;
            }
        } catch (IOException | IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }

    private void acknowledgeProxy() {
        if (!connected) {
            connected = true;

            // Optionally, log successful acknowledgement
            //getLogger().info("Proxy acknowledged successfully.");
        } else {
            // Log miscommunication
            //getLogger().warning("Miscommunication with the service.");
        }
    }

    public void sendMessage(final @NotNull MessageType messageType,
                            final @NotNull Consumer<ByteArrayDataOutput> outputConsumer) {
        for (Player player : server.getOnlinePlayers()) {
            ByteArrayDataOutput msg = ByteStreams.newDataOutput();
            outputConsumer.accept(msg);
            player.sendPluginMessage(plugin, Constants.PROXY_CHANNEL_NAME, msg.toByteArray());
            break;
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
