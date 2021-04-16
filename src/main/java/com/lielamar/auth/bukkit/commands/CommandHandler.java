package com.lielamar.auth.bukkit.commands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.commands.subcommands.*;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class CommandHandler implements CommandExecutor {

    private final TwoFactorAuthentication main;
    private final Set<Command> commands;
    private Command loginCommand, setupCommand;

    public CommandHandler(TwoFactorAuthentication main) {
        this.main = main;
        this.main.getCommand(Constants.mainCommand).setExecutor(this);

        this.commands = new HashSet<>();
        this.setupCommands();
    }

    /**
     * Sets up all of the sub commands of /2fa
     */
    private void setupCommands() {
        loginCommand = new LoginCommand("", main);
        setupCommand = new SetupCommand("", main);
        commands.add(new EnableCommand(Constants.enableCommand, main));
        commands.add(new DisableCommand(Constants.disableCommand, main));
        commands.add(new ReloadCommand(Constants.reloadCommand, main));
        commands.add(new HelpCommand(Constants.helpCommand, main, commands));
    }

    /**
     * Returns a {@link com.lielamar.lielsutils.commands.Command} object related to the given name
     *
     * @param name   Name/Alias of the command to return
     * @return       The command object from the commands set
     */
    public Command getCommand(String name) {
        for(Command cmd : this.commands) {
            if(cmd.getName() != null) {
                if(cmd.getName().equalsIgnoreCase(name))
                    return cmd;
            }

            if(cmd.getAliases() != null) {
                for(String s : cmd.getAliases()) {
                    if(s.equalsIgnoreCase(name))
                        return cmd;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull org.bukkit.command.Command cmd, @Nonnull String cmdLabel, @Nonnull String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if(main.getAuthHandler() == null) {
                main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SOMETHING_WENT_WRONG);
                return false;
            } else if(main.getAuthHandler().getAuthState(player.getUniqueId()) == null) {
                main.getAuthHandler().playerJoin(player.getUniqueId());
            }

            if(args.length == 0) { // If we don't receive an argument we want to either execute the help command if they have 2fa setup, or execute the setup (enable) command if they don't have it setup.
                Command subCommand = (main.getAuthHandler().is2FAEnabled(player.getUniqueId()) ? getCommand(Constants.helpCommand) : getCommand(Constants.enableCommand));
                if(subCommand != null) {
                    subCommand.execute(commandSender, args);
                    return true;
                }
            } else { // If we received an argument, we want to try to authenticate the user
                if(main.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.PENDING_LOGIN) {
                    loginCommand.execute(player, args);
                    return false;
                } else if(main.getAuthHandler().isPendingSetup(player.getUniqueId())) {
                    setupCommand.execute(player, args);
                    return false;
                }
            }
        }

        if(args.length == 0) {
            Command subCommand = getCommand(Constants.helpCommand);
            if(subCommand != null) subCommand.execute(commandSender, args);
        } else {
            Command subCommand = getCommand(args[0]);
            if(subCommand == null) subCommand = getCommand(Constants.helpCommand);

            String[] arguments = new String[args.length-1];
            System.arraycopy(args, 1, arguments, 0, arguments.length);
            subCommand.execute(commandSender, arguments);
        }

        return false;
    }
}