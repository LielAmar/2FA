package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.communication.BasicAuthCommunication;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OnPlayerConnection implements Listener {

    private final TwoFactorAuthentication plugin;

    public OnPlayerConnection(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getAuthHandler().playerJoin(player.getUniqueId());


        if(this.plugin.getAuthHandler().getAuthCommunicationHandler() == null)
            return;

        // If we have the communication-method set to something other than NONE, we don't need to do any checks.
        if(!(this.plugin.getAuthHandler().getAuthCommunicationHandler() instanceof BasicAuthCommunication))
            return;


        // Sending a request to proxy to check whether there is a proxy or not :)
        this.plugin.getAuthHandler().getAuthCommunicationHandler().checkCommunication(player.getUniqueId(),
                new AuthCommunicationHandler.AuthCommunicationCallback() {

                    @Override
                    public void execute(AuthHandler.AuthState authState) {
                        Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission(Constants.alertsPermission)).forEach(pl ->
                                plugin.getMessageHandler().sendMessage(pl, MessageHandler.TwoFAMessages.COMMUNICATION_METHOD_NOT_CORRECT));
                    }

                    public void onTimeout() {}
                    public long getExecutionStamp() { return System.currentTimeMillis(); }
                    public UUID getPlayerUUID() { return player.getUniqueId(); }
                });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getAuthHandler().playerQuit(player.getUniqueId());
    }
}
