package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.communication.BasicAuthCommunication;
import com.lielamar.auth.bukkit.communication.ProxyAuthCommunication;
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
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OnPlayerConnection implements Listener {

    private final TwoFactorAuthentication plugin;

    private boolean checkedProxy;

    public OnPlayerConnection(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        this.checkedProxy = false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getAuthHandler().changeState(player.getUniqueId(), AuthHandler.AuthState.PENDING_LOGIN);

        // Adding a 1 tick delay so requests to proxy are sent correctly
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.plugin.getAuthHandler().playerJoin(player.getUniqueId());

            if (!this.checkedProxy) {
                this.checkProxy(player);
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.plugin.getAuthHandler().isPendingSetup(player.getUniqueId())) {
            this.plugin.getAuthHandler().cancelKey(player.getUniqueId());
            this.plugin.getAuthHandler().removeQRItem(player);
        }

        this.plugin.getAuthHandler().playerQuit(player.getUniqueId());
    }

    private void checkProxy(Player player) {
        // Checking if we even have an auth communication handler
        if (this.plugin.getAuthHandler().getAuthCommunicationHandler() == null) {
            return;
        }

        // If we have the communication-method set to something other than NONE, we don't need to do any checks.
        if (!(this.plugin.getAuthHandler().getAuthCommunicationHandler() instanceof BasicAuthCommunication)) {
            return;
        }

        // Sending a request to proxy to check whether there is a proxy or not :)
        AuthCommunicationHandler proxyAuthComm = new ProxyAuthCommunication(this.plugin);
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, Constants.PROXY_CHANNEL_NAME);
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, Constants.PROXY_CHANNEL_NAME,
                (PluginMessageListener) proxyAuthComm);

        proxyAuthComm.checkCommunication(player.getUniqueId(), new AuthCommunicationHandler.AuthCommunicationCallback() {

            @Override
            public void execute(AuthHandler.AuthState authState) {
                Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission(Constants.alertsPermission)).forEach(pl
                        -> plugin.getMessageHandler().sendMessage(pl, MessageHandler.TwoFAMessages.COMMUNICATION_METHOD_NOT_CORRECT));

                plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, Constants.PROXY_CHANNEL_NAME,
                        (PluginMessageListener) proxyAuthComm);
            }

            @Override
            public void onTimeout() {
            }

            @Override
            public long getExecutionStamp() {
                return System.currentTimeMillis();
            }

            @Override
            public UUID getPlayerUUID() {
                return player.getUniqueId();
            }
        });

        this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, Constants.PROXY_CHANNEL_NAME);

        this.checkedProxy = true;
    }
}
