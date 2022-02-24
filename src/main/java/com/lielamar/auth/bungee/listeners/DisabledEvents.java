package com.lielamar.auth.bungee.listeners;

import com.lielamar.auth.bungee.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisabledEvents implements Listener {

    private final TwoFactorAuthentication main;

    public DisabledEvents(TwoFactorAuthentication main) {
        this.main = main;
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        if(!event.getMessage().startsWith("/")) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            String[] args = event.getMessage().substring(1).split("\\s+");

            if(this.main.getConfigHandler().isDisableCommands()) {
                if(args.length > 0) {
                    String command = args[0];
                    if(!this.main.getConfigHandler().getWhitelistedCommands().contains(command) && !Constants.mainCommand.equalsIgnoreCase(command)) {
                        event.setCancelled(true);
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    }
                }
            } else {
                if(args.length > 0) {
                    String command = args[0];
                    if(this.main.getConfigHandler().getBlacklistedCommands().contains(command)) {
                        event.setCancelled(true);
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if(event.getMessage().startsWith("/")) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            if(this.main.getConfigHandler().isDisableChat())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            if(this.main.getConfigHandler().isDisableServerSwitch()) {
                event.setCancelled(true);
                main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }
}
