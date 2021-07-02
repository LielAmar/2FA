package com.lielamar.auth.velocity.listeners;

import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;

public class DisabledEvents {

    private final TwoFactorAuthentication main;

    public DisabledEvents(TwoFactorAuthentication main) {
        this.main = main;
    }

    @Subscribe
    public void onMessage(PlayerChatEvent event) {
        if(!event.getMessage().startsWith("/")) return;

        Player player = event.getPlayer();

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            String[] args = event.getMessage().substring(1).split("\\s+");
//            if(this.main.getConfigHandler().isCommandsDisabled()) {
                if(args.length > 0) {
                    String command = args[0];
//                    if(!this.main.getConfigHandler().getWhitelistedCommands().contains(command) && !Constants.mainCommand.equalsIgnoreCase(command)) {
                        event.setResult(PlayerChatEvent.ChatResult.denied());
//                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    }
                }
//            } else {
//                if(args.length > 0) {
//                    String command = args[0];
//                    if(this.main.getConfigHandler().getBlacklistedCommands().contains(command)) {
//                        event.setResult(PlayerChatEvent.ChatResult.denied());
//                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
//                    }
//                }
//            }
//        }
    }

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();

        if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
//            if(this.main.getConfigHandler().isDisableServerSwitch()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
//                main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
//            }
        }
    }
}
