package com.lielamar.auth.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lielamar.auth.bungee.Main;
import com.lielamar.auth.shared.handlers.AuthHandler;
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

    private final Main main;
    public OnPluginMessage(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onQueryReceive(PluginMessageEvent event) {
        if(!event.getTag().equals(Constants.channelName)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();                                    // Getting the SubChannel name

        if(subChannel.equals(Constants.subChannelName)) {
            UUID playerUUID = UUID.fromString(in.readUTF());                 // UUID of the player to run the check on
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUUID);

            short length = in.readShort();                                   // Length of the message
            byte[] msgBytes = new byte[length];                              // Message itself
            in.readFully(msgBytes);

            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String action = msgIn.readUTF();                             // The message action (isAuthenticated/setAuthenticated)

                if(action.equals(Constants.getState)) {
                    AuthHandler.AuthState defaultState = AuthHandler.AuthState.valueOf(msgIn.readUTF());
                    if(main.getAuthHandler().getAuthState(player.getUniqueId()) == null)
                        main.getAuthHandler().changeState(player.getUniqueId(), defaultState);

                    sendResponse(player, action);
                } else if(action.equals(Constants.setState)) {
                    AuthHandler.AuthState state = AuthHandler.AuthState.valueOf(msgIn.readUTF());
                    if(state != main.getAuthHandler().getAuthState(player.getUniqueId()))
                        main.getAuthHandler().changeState(player.getUniqueId(), state);

                    sendResponse(player, action);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends back a response from BungeeCord to spigot
     *
     * @param player   Player to send the response of
     * @param action   The action of the message (isAuthenticated/setAuthenticated)
     */
    public void sendResponse(ProxiedPlayer player, String action) {
        AuthHandler.AuthState state = main.getAuthHandler().getAuthState(player.getUniqueId());

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Constants.subChannelName);                        // Setting the SubChannel of the response
        out.writeUTF(player.getUniqueId().toString());                 // Setting the playerUUID of the response

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        try {
            msgOut.writeUTF(action);                                   // Setting the action of the response
            if(state != null)
                msgOut.writeUTF(state.name());                         // Setting the value of the response
            else
                msgOut.writeUTF("null");
        } catch(IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgBytes.toByteArray().length);                 // Setting the message length
        out.write(msgBytes.toByteArray());                             // Setting the message data as ByteArray

        player.getServer().getInfo().sendData(Constants.channelName, out.toByteArray());
    }
}
