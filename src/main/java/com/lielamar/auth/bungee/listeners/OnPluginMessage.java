package com.lielamar.auth.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.bungee.Main;
import com.lielamar.auth.bungee.BungeeMessagingUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class OnPluginMessage implements Listener {

    private final Main main;
    public OnPluginMessage(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onQueryReceive(PluginMessageEvent event) {
        if(!event.getTag().equals(BungeeMessagingUtils.channelName)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();                                    // Getting the SubChannel name

        if(subChannel.equals(BungeeMessagingUtils.subChannelName)) {
            UUID playerUUID = UUID.fromString(in.readUTF());                 // UUID of the player to run the check on
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);

            short length = in.readShort();                                   // Length of the message
            byte[] msgBytes = new byte[length];                              // Message itself
            in.readFully(msgBytes);

            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String action = msgIn.readUTF();                             // The message action (isAuthenticated/setAuthenticated)

                // If the action is a 2FA's action, specifically isAuthenticated, return whether the player is BungeeCord authenticated
                if(action.equals(BungeeMessagingUtils.isAuthenticated)) {
                    sendResponse(player, action, main.getAuthHandler().containsPlayer(player));

                // If the action is a 2FA's action, specifically setAuthenticated, set the BungeeCord authentication of the player to the given value
                } else if(action.equals(BungeeMessagingUtils.setAuthenticated)) {
                    boolean authenticated = msgIn.readBoolean();
                    if(authenticated)
                        main.getAuthHandler().setPlayer(player);
                    else
                        main.getAuthHandler().removePlayer(player);

                    sendResponse(player, action, main.getAuthHandler().containsPlayer(player));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends back a response from BungeeCord to spigot
     *
     * @param player          Player to send the response of
     * @param action          The action of the message (isAuthenticated/setAuthenticated)
     * @param authenticated   Whether the player is authenticated on BungeeCord
     */
    public void sendResponse(ProxiedPlayer player, String action, boolean authenticated) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(BungeeMessagingUtils.subChannelName);             // Setting the SubChannel of the response

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(action);                                   // Setting the action of the response
            msgOut.writeBoolean(authenticated);                        // Setting the value of the response
        } catch(IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);                 // Setting the message length
        out.write(msgBytes.toByteArray());                             // Setting the message data as ByteArray

        player.getServer().getInfo().sendData(BungeeMessagingUtils.channelName, out.toByteArray());
    }
}
