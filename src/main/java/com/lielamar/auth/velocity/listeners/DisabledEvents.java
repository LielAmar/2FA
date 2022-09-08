package com.lielamar.auth.velocity.listeners;

import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.lielamar.auth.velocity.handlers.MessageHandler;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class DisabledEvents {

    private final TwoFactorAuthentication plugin;

    public DisabledEvents(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onMessage(CommandExecuteEvent event) {
        CommandSource source = event.getCommandSource();

        if (!(source instanceof Player)) {
            return;
        }

        Player player = (Player) source;

        if (this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            String command = event.getCommand();

            if (this.plugin.getConfigHandler().isDisableCommands()) {
                if (!this.plugin.getConfigHandler().getWhitelistedCommands().contains(command) && !command.toLowerCase(Locale.ROOT)
                        .startsWith(Constants.MAIN_COMMAND.getA().toLowerCase(Locale.ROOT))) {
                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                    this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                }
            } else {
                if (this.plugin.getConfigHandler().getBlacklistedCommands().contains(command)) {
                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                    this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                }
            }
        }
    }

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();

        if (this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
            if (this.plugin.getConfigHandler().isDisableServerSwitch()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }
}
