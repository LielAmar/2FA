package com.lielamar.auth.bukkit.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class BungeecordMessageHandler implements PluginMessageListener {

    private final Main main;
    public BungeecordMessageHandler(Main main) {
        this.main = main;
    }

    /**
     * Communicates with BungeeCord and sets the player authentication state to {authenticated}
     *
     * @param uuid    UUID of the player to set (un)authenticated
     * @param state   AuthState to set for the player on BungeeCord
     */
    public void setBungeeCordAuthState(UUID uuid, AuthHandler.AuthState state) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(Constants.subChannelName);            // Setting the SubChannel of the message
        out.writeUTF(uuid.toString());                                // Setting the UUID of the player

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(Constants.setState);           // Setting the action of the message
            msgOut.writeUTF(state.name());                            // Setting the state of the player
        } catch(IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);                 // Setting the message length
        out.write(msgBytes.toByteArray());                             // Setting the message data as ByteArray

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
            player.sendPluginMessage(main, Constants.channelName, out.toByteArray());
    }

    /**
     * Communicates with BungeeCord and gets the player authentication state
     *
     * @param uuid            UUID of the player to get check if authenticated
     */
    public void getBungeeCordAUthState(UUID uuid) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(Constants.subChannelName);            // Setting the SubChannel of the message
        out.writeUTF(uuid.toString());                                // Setting the UUID of the player

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(Constants.getState);           // Setting the action of the message
            msgOut.writeUTF(main.getAuthHandler().getAuthState(uuid).name()); // Default auth state
        } catch(IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);                 // Setting the message length
        out.write(msgBytes.toByteArray());                             // Setting the message data as ByteArray

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
            player.sendPluginMessage(main, Constants.channelName, out.toByteArray());
    }

    /**
     * Handles the responses from BungeeCord for the above methods
     *
     * @param channel   Communication channel
     * @param player    Player involved
     * @param message   Content of the response
     */
    @Override
    public void onPluginMessageReceived(String channel, @Nonnull Player player, @Nonnull byte[] message) {
        if(!channel.equals(Constants.channelName)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();                           // Getting the SubChannel name
        UUID playerUUID = UUID.fromString(in.readUTF());            // UUID of the player to run the check on

        // If the SubChannel name is the 2FA's SubChannel name
        if(subChannel.equals(Constants.subChannelName)) {
            short length = in.readShort();                          // Length of the message
            byte[] msgBytes = new byte[length];                     // Message itself
            in.readFully(msgBytes);

            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String action = msgIn.readUTF();                    // The message action (isAuthenticated/setAuthenticated)
                AuthHandler.AuthState state;                        // Default response value

                if(action.equals(Constants.getState)) {
                    state = AuthHandler.AuthState.valueOf(msgIn.readUTF());
                    main.getAuthHandler().changeState(playerUUID, state);
                } else if(action.equals(Constants.setState)) {
                    state = AuthHandler.AuthState.valueOf(msgIn.readUTF());
                    if(state != main.getAuthHandler().getAuthState(playerUUID)) {
                        main.getAuthHandler().changeState(playerUUID, state);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}