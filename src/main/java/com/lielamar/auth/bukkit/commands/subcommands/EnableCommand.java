package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import com.lielamar.lielsutils.bukkit.commands.SuperCommand;
import com.lielamar.lielsutils.bukkit.commands.TabOptionsBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnableCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public EnableCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.enableCommand.getA(), Constants.enableCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
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
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return new TabOptionsBuilder().player().build(args);
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.parent.noPermissionEvent(commandSender);
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