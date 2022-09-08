package com.lielamar.auth.bukkit.commands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.commands.subcommands.*;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.Command;
import com.lielamar.lielsutils.bukkit.commands.SuperCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TwoFactorAuthenticationCommand extends SuperCommand {

    private final TwoFactorAuthentication plugin;
    private final Command[] commands;
    private final Command helpCommand, loginCommand, setupCommand;

    public TwoFactorAuthenticationCommand(TwoFactorAuthentication plugin) {
        super(Constants.MAIN_COMMAND.getA(), Constants.MAIN_COMMAND.getB());

        this.plugin = plugin;

        this.commands = new Command[] {
                new LoginCommand(plugin, this),
                new SetupCommand(plugin, this),
                new EnableCommand(plugin, this),
                new DisableCommand(plugin, this),
                new CancelCommand(plugin, this),
                new ReloadCommand(plugin, this),
                new ReportCommand(plugin, this),
                new HelpCommand(plugin, this)};

        this.helpCommand = super.getSubCommand("help");
        this.loginCommand = super.getSubCommand("login");
        this.setupCommand = super.getSubCommand("setup");
    }


    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if(plugin.getAuthHandler() == null) {
                plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SOMETHING_WENT_WRONG);
                return false;
            }

            if(plugin.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.NONE)
                plugin.getAuthHandler().playerJoin(player.getUniqueId());

            // If we don't receive an argument we want to either execute the help command if they have 2fa setup, or execute the setup (enable) command if they don't have it setup.
            if(args.length == 0) {
                if(this.helpCommand != null)
                    this.helpCommand.execute(commandSender, args);
                
                return false;
            }

            if(plugin.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.PENDING_LOGIN) {
                if(this.loginCommand != null)
                    this.loginCommand.execute(player, args);

                return false;
            } else if(plugin.getAuthHandler().isPendingSetup(player.getUniqueId())) {
                Command subCommand = super.getSubCommand(args[0]);

                if(subCommand instanceof CancelCommand)
                    subCommand.execute(player, args);
                else if(this.setupCommand != null)
                    this.setupCommand.execute(player, args);
            }

            return false;
        }

        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        return new ArrayList<>();
    }

    @Override
    public void subCommandNotFoundEvent(@NotNull CommandSender commandSender) {
        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.COMMAND_NOT_FOUND);
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
    }

    @Override
    public @NotNull Command[] getSubCommands() {
        return this.commands;
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }

    @Override
    public @NotNull String getDescription() {
        return "";
    }

    @Override
    public @NotNull String[] getAliases() {
        return new String[0];
    }
}