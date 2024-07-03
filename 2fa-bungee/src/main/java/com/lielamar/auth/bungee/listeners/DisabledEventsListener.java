package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.TwoFactorAuthPlugin;
import com.lielamar.auth.bungee.handlers.BungeeAuthHandler;
import com.lielamar.auth.bungee.handlers.BungeeConfigHandler;
import com.lielamar.auth.core.handlers.AbstractConfigHandler;
import com.lielamar.auth.core.utils.Constants;
import jakarta.inject.Inject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class DisabledEventsListener implements Listener {
    private final BungeeAuthHandler authHandler;
    private final BungeeConfigHandler configHandler;

    @Inject
    public DisabledEventsListener(final @NotNull BungeeAuthHandler authHandler,
                                  final @NotNull BungeeConfigHandler configHandler) {
        this.authHandler = authHandler;
        this.configHandler = configHandler;
    }


    @EventHandler
    public void onCommand(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (authHandler.needsToAuthenticate(player.getUniqueId())) {
            String[] args = event.getMessage().substring(1).split("\\s+");

            if (configHandler.isDisableCommands()) {
                if (args.length > 0) {
                    String command = args[0];
                    if (!configHandler.getWhitelistedCommands().contains(command) && !Constants.mainCommand.getA().equalsIgnoreCase(command)) {
                        event.setCancelled(true);
                        this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    }
                }
            } else {
                if (args.length > 0) {
                    String command = args[0];
                    if (configHandler.getBlacklistedCommands().contains(command)) {
                        event.setCancelled(true);
                        this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getMessage().startsWith("/")) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (authHandler.needsToAuthenticate(player.getUniqueId())) {
            if (configHandler.isDisableChat()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (authHandler.needsToAuthenticate(player.getUniqueId())) {
            if (configHandler.isDisableServerSwitch()) {
                event.setCancelled(true);
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }

    @EventHandler
    public void onServerConnect(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (authHandler.needsToAuthenticate(player.getUniqueId())) {
            if (configHandler.isDisableServerSwitch()) {
                event.setCancelled(true);
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }
}