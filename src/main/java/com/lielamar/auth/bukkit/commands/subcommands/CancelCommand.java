package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.utils.cmd.StandaloneCommand;
import com.lielamar.auth.bukkit.utils.cmd.SuperCommand;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CancelCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public CancelCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.cancelCommand.getA(), Constants.cancelCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
            return false;
        }

        Player player = (Player) commandSender;

        if (this.plugin.getAuthHandler().isPendingSetup(player.getUniqueId())) {
            if (this.plugin.getAuthHandler().cancelKey(player.getUniqueId())) {
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.CANCELED_SETUP);
            }

            this.plugin.getAuthHandler().removeQRItem(player);
        } else {
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.NOT_IN_SETUP_MODE);
        }

        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.parent.noPermissionEvent(commandSender);
    }

    @Override
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_CANCEL_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cancel", "stop"};
    }
}
