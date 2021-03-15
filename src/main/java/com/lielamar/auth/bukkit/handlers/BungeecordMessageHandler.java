package com.lielamar.auth.bukkit.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.Callback;
import com.lielamar.auth.shared.handlers.PluginMessagingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class BungeecordMessageHandler extends PluginMessagingHandler implements PluginMessageListener {

    private final TwoFactorAuthentication main;
    private final Map<UUID, Callback> callbackFunctions;

    public BungeecordMessageHandler(TwoFactorAuthentication main) {
        this.main = main;
        this.callbackFunctions = new HashMap<>();

        // Looping over the callbacks, if it's been more than 15 seconds cancel the callback
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            long currentTimestamp = System.currentTimeMillis();

            Iterator<UUID> iterator = callbackFunctions.keySet().iterator();
            UUID key;
            Callback value;
            while(iterator.hasNext()) {
                key = iterator.next();
                value = callbackFunctions.get(key);

                if(value != null) {
                    if((currentTimestamp - value.getExecutionStamp())/1000 > 15)
                        iterator.remove();
                }
            }
        }, 300L, 300L);
    }

    /**
     * Sets the header of a message
     *
     * @param msg        Message Stream
     * @param uuid       UUID of the player attached to the message
     * @param callback   Callback function to call once a response is received
     */
    public void setMessageHeader(ByteArrayDataOutput msg, UUID uuid, Callback callback) {
        msg.writeUTF(super.subChannelName);                          // Setting the SubChannel of the message
        msg.writeUTF(attachCallbackFunction(callback).toString());   // Setting the Message UUID
        msg.writeUTF(uuid.toString());                               // Setting the UUID of the player
    }

    /**
     * Sets the body of a message
     *
     * @param msgBody      Message Body
     * @param action       Action
     * @param parameters   Parameters to set in the body
     */
    public void setMessageBody(ByteArrayOutputStream msgBody, MessageAction action, String... parameters) {
        DataOutputStream bodyData = new DataOutputStream(msgBody);
        try {
            bodyData.writeUTF(action.name());   // Setting the action of the message

            for(String param : parameters)      // Setting the parameters
                bodyData.writeUTF(param);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Applies the message body
     *
     * @param msg       Message Stream
     * @param msgBody   Message Body
     */
    public void applyMessageBody(ByteArrayDataOutput msg, ByteArrayOutputStream msgBody) {
        msg.writeShort(msgBody.toByteArray().length);   // Setting the body length
        msg.write(msgBody.toByteArray());               // Setting the body data
    }

    /**
     * Sends the message to bungeecord
     *
     * @param uuid   Player attached to the message
     * @param msg    Message object as ByteArray
     */
    public void sendMessage(UUID uuid, ByteArrayDataOutput msg) {
        Player player = Bukkit.getPlayer(uuid);

        if(player != null && player.isOnline())
            player.sendPluginMessage(this.main, super.channelName, msg.toByteArray());
    }


    /**
     * Generates a random UUID for the message and attaches the callback function to call it later when a response is made
     *
     * @param callback   Callback function to save
     * @return           Random generated UUID
     */
    public UUID attachCallbackFunction(Callback callback) {
        UUID randomUUID = UUID.randomUUID();
        this.callbackFunctions.put(randomUUID, callback);
        return randomUUID;
    }


    /**
     * Communicates with BungeeCord and sets the player authentication state to {authenticated}
     *
     * @param uuid       UUID of the player to set (un)authenticated
     * @param state      AuthState to set for the player on BungeeCord
     * @param callback   A callback function to call whenever a response for the message is received
     */
    public void setBungeeCordAuthState(UUID uuid, AuthHandler.AuthState state, Callback callback) {
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        this.setMessageHeader(msg, uuid, callback);

        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        this.setMessageBody(msgBody, MessageAction.SET_STATE, state.name());
        this.applyMessageBody(msg, msgBody);

        this.sendMessage(uuid, msg);
    }

    public void setBungeeCordAuthState(UUID uuid, AuthHandler.AuthState state) {
        this.setBungeeCordAuthState(uuid, state, null);
    }


    /**
     * Communicates with BungeeCord and gets the player authentication state
     *
     * @param uuid       UUID of the player to get check if authenticated
     * @param callback   A callback function to call whenever a response for the message is received
     */
    public void getBungeeCordAuthState(UUID uuid, AuthHandler.AuthState defaultState, Callback callback) {
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        this.setMessageHeader(msg, uuid, callback);

        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        this.setMessageBody(msgBody, MessageAction.GET_STATE, defaultState.name());
        this.applyMessageBody(msg, msgBody);

        this.sendMessage(uuid, msg);
    }

    public void getBungeeCordAuthState(UUID uuid, AuthHandler.AuthState defaultState) {
        this.getBungeeCordAuthState(uuid, defaultState, null);
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
        // If the Channel name is not the 2FA's Channel name we want to return
        if(!channel.equals(super.channelName)) return;

        ByteArrayDataInput response = ByteStreams.newDataInput(message);
        String subChannel = response.readUTF();
        UUID messageUUID = UUID.fromString(response.readUTF());
        UUID playerUUID = UUID.fromString(response.readUTF());

        // If the SubChannel name is the 2FA's SubChannel name
        if(subChannel.equals(super.subChannelName)) {
            short bodyLength = response.readShort();
            byte[] msgBody = new byte[bodyLength];
            response.readFully(msgBody);

            DataInputStream msgBodyData = new DataInputStream(new ByteArrayInputStream(msgBody));
            try {
                MessageAction action = MessageAction.valueOf(msgBodyData.readUTF());

                if(action == MessageAction.GET_STATE) {
                    AuthHandler.AuthState state = AuthHandler.AuthState.valueOf(msgBodyData.readUTF());
                    main.getAuthHandler().changeState(playerUUID, state, false);
                }

                Callback callback = this.callbackFunctions.getOrDefault(messageUUID, null);
                if(callback != null) callback.execute();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}