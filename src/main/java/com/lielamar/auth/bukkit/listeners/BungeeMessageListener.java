package com.lielamar.auth.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.bungee.BungeeMessagingUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class BungeeMessageListener implements PluginMessageListener {

    private final Main main;
    public BungeeMessageListener(Main main) {
        this.main = main;
    }

    /**
     * Communicates with BungeeCord and sets the player authentication state to {authenticated}
     *
     * @param uuid            UUID of the player to set (un)authenticated
     * @param authenticated   Whether to set to authenticated or unauthenticated
     */
    public void setBungeeCordAuthenticated(UUID uuid, boolean authenticated) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(BungeeMessagingUtils.subChannelName);            // Setting the SubChannel of the message
        out.writeUTF(uuid.toString());                                // Setting the UUID of the player

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(BungeeMessagingUtils.setAuthenticated);   // Setting the action of the message
            msgOut.writeBoolean(authenticated);                       // Setting whether to authenticate the player on BungeeCord
        } catch(IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);                 // Setting the message length
        out.write(msgBytes.toByteArray());                             // Setting the message data as ByteArray

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
            player.sendPluginMessage(main, BungeeMessagingUtils.channelName, out.toByteArray());
    }

    /**
     * Communicates with BungeeCord and gets the player authentication state
     *
     * @param uuid            UUID of the player to get check if authenticated
     */
    public void isBungeeCordAuthenticated(UUID uuid) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(BungeeMessagingUtils.subChannelName);            // Setting the SubChannel of the message
        out.writeUTF(uuid.toString());                                // Setting the UUID of the player

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(BungeeMessagingUtils.isAuthenticated);    // Setting the action of the message
        } catch(IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);                 // Setting the message length
        out.write(msgBytes.toByteArray());                             // Setting the message data as ByteArray

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
            player.sendPluginMessage(main, BungeeMessagingUtils.channelName, out.toByteArray());
    }

    /**
     * Handles the responses from BungeeCord for the above methods
     *
     * @param channel   Communication channel
     * @param player    Player involved
     * @param message   Content of the response
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals(BungeeMessagingUtils.channelName)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();                           // Getting the SubChannel name

        // If the SubChannel name is the 2FA's SubChannel name
        if(subChannel.equals(BungeeMessagingUtils.subChannelName)) {
            short length = in.readShort();                          // Length of the message
            byte[] msgBytes = new byte[length];                     // Message itself
            in.readFully(msgBytes);

            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String action = msgIn.readUTF();                    // The message action (isAuthenticated/setAuthenticated)
                boolean authenticated = false;                      // Default response value

                // If the action is a 2FA's action (isAuthenticated/setAuthenticated), set the authenticated variable to the value received.
                if(action.equals(BungeeMessagingUtils.isAuthenticated) || action.equalsIgnoreCase(BungeeMessagingUtils.setAuthenticated))
                    authenticated = msgIn.readBoolean();

                // If we received a `true` value, meaning the player is authenticated on BungeeCord, set the player to be authenticated on spigot as well
                if(authenticated)
                    main.getAuthHandler().changeState(player.getUniqueId(), AuthHandler.AuthState.AUTHENTICATED);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
