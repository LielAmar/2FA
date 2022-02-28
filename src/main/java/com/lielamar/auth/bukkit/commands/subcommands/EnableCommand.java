package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import com.lielamar.lielsutils.bukkit.commands.TabOptionsBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnableCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;

    public EnableCommand(TwoFactorAuthentication plugin) {
        super(Constants.enableCommand.getA(), Constants.enableCommand.getB());

        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] targets) {
        if(!(commandSender instanceof Player)) {
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
            return false;
        }

        Player player = (Player) commandSender;

        if(this.plugin.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.DISABLED) {
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.GENERATING_KEY);
            this.plugin.getAuthHandler().createKey(player.getUniqueId());
        } else if(this.plugin.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.PENDING_SETUP)
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.ALREADY_IN_SETUP_MODE);
        else
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.ALREADY_SETUP);

        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        return new TabOptionsBuilder().player().build(new String[0]);
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
    }

    @Override
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_ENABLE_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[] { "add", "enable", "setup", "on", "activate", "true" };
    }
}