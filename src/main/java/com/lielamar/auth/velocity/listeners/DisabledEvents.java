package com.lielamar.auth.velocity.listeners;

import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.lielamar.auth.velocity.handlers.MessageHandler;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;

import java.util.Locale;

public class DisabledEvents {

    private final TwoFactorAuthentication main;

    public DisabledEvents(TwoFactorAuthentication main) {
        this.main = main;
    }

    @Subscribe
    public void onMessage(CommandExecuteEvent event) {
        CommandSource source = event.getCommandSource();

        System.out.println("1");

        if(!(source instanceof Player))
            return;
        System.out.println("2");

        Player player = (Player) source;

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            String command = event.getCommand();
            System.out.println("3");

            if(this.main.getConfigHandler().isDisableCommands()) {
                System.out.println("4");

                if(!this.main.getConfigHandler().getWhitelistedCommands().contains(command) && !command.toLowerCase(Locale.ROOT).startsWith(Constants.mainCommand.toLowerCase(Locale.ROOT))) {
                    System.out.println("5");

                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                }
            } else {
                System.out.println("6");

                if(this.main.getConfigHandler().getBlacklistedCommands().contains(command)) {
                    System.out.println("7");

                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                }
            }
        }
    }

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            if(this.main.getConfigHandler().isDisableServerSwitch()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }
}
