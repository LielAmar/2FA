package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.Main;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.utils.Constants;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisabledEvents implements Listener {

    private final Main main;
    public DisabledEvents(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onMessage(ChatEvent event) {
        if(!event.getMessage().startsWith("/")) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if(this.main.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.PENDING_LOGIN) {
            String[] args = event.getMessage().substring(1).split("\\s+");
            if(this.main.getConfigHandler().isCommandsDisabled()) {
                if(args.length > 0) {
                    String command = args[0];
                    if(!this.main.getConfigHandler().getWhitelistedCommands().contains(command) && !Constants.mainCommand.equalsIgnoreCase(command)) {
                        event.setCancelled(true);
                        main.getMessageHandler().sendMessage(player, "&cPlease validate your account with two-factor authentication");
                    }
                }
            } else {
                if(args.length > 0) {
                    String command = args[0];
                    if(this.main.getConfigHandler().getBlacklistedCommands().contains(command)) {
                        event.setCancelled(true);
                        main.getMessageHandler().sendMessage(player, "&cPlease validate your account with two-factor authentication");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(this.main.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.PENDING_LOGIN) {
            if(this.main.getConfigHandler().isDisableServerSwitch()) {
                event.setCancelled(true);
                main.getMessageHandler().sendMessage(player, "&cPlease validate your account with two-factor authentication");
            }
        }
    }
}
