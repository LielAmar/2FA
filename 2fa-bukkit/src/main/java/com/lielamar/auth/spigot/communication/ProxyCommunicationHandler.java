package com.lielamar.auth.spigot.communication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.api.communication.MessageType;
import com.lielamar.auth.core.utils.Constants;
import jakarta.inject.Inject;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProxyCommunicationHandler implements PluginMessageListener {
    private final Server server;

    private boolean connected;

    @Inject
    public ProxyCommunicationHandler(final @NotNull Server server) {
        this.server = server;
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
            server.sendPluginMessage();
            // Optionally, log successful acknowledgement
            //getLogger().info("Proxy acknowledged successfully.");
        } else {
            // Log miscommunication
            //getLogger().warning("Miscommunication with the service.");
        }
    }

    public void sendMessage(MessageType messageType, List<?> message) {
        // Implement sending messages to the proxy
    }
}
