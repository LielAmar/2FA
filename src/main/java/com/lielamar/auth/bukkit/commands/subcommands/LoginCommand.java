package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.events.PlayerFailedAuthenticationEvent;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import com.lielamar.lielsutils.bukkit.commands.SuperCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LoginCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public LoginCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.LOGIN_COMMAND.getA(), Constants.LOGIN_COMMAND.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
            return false;
        }

        Player player = (Player) commandSender;

        StringBuilder code = new StringBuilder();

        for (String arg : args) {
            code.append(arg);
        }

        try {
            boolean isValid = this.plugin.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(code.toString()));

            if (isValid) {
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SUCCESSFULLY_AUTHENTICATED);
            } else {
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INCORRECT_CODE);

                PlayerFailedAuthenticationEvent event = new PlayerFailedAuthenticationEvent(player,
                        this.plugin.getAuthHandler().increaseFailedAttempts(player.getUniqueId(), 1));
                Bukkit.getPluginManager().callEvent(event);
            }
        } catch (NumberFormatException exception) {
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INVALID_CODE);
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
    public String[] getAliases() {
        return new String[0];
    }
}
