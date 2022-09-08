package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class DisabledEvents implements Listener {

    private final TwoFactorAuthentication plugin;

    public DisabledEvents(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (!this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            return;
        }

        String[] args = event.getMessage().substring(1).split("\\s+");

        if (this.plugin.getConfigHandler().isDisableCommands()) {
            if (args.length == 0) {
                return;
            }

            String command = args[0];
            if (!this.plugin.getConfigHandler().getWhitelistedCommands().contains(command) && !Constants.MAIN_COMMAND.getA().equalsIgnoreCase(command)) {
                event.setCancelled(true);
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        } else {
            if (args.length == 0) {
                return;
            }

            String command = args[0];
            if (this.plugin.getConfigHandler().getBlacklistedCommands().contains(command)) {
                event.setCancelled(true);
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getMessage().startsWith("/")) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (!this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            return;
        }

        if (this.plugin.getConfigHandler().isDisableChat()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            return;
        }

        if (this.plugin.getConfigHandler().isDisableServerSwitch()) {
            event.setCancelled(true);
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
        }
    }
}
