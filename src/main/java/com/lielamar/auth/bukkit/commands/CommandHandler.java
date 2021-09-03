package com.lielamar.auth.bukkit.commands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.commands.subcommands.*;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final TwoFactorAuthentication plugin;
    private final Set<Command> commands;
    private Command loginCommand, setupCommand;

    public CommandHandler(TwoFactorAuthentication plugin) {
        this.plugin = plugin;
        this.commands = new HashSet<>();

        if(!Bukkit.getPluginManager().isPluginEnabled(plugin)) return;

        PluginCommand pluginCommand = this.plugin.getCommand(Constants.mainCommand);
        if(pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }


        this.setupCommands();
    }

    /**
     * Sets up all of the sub commands of /2fa
     */
    private void setupCommands() {
        loginCommand = new LoginCommand("", plugin);
        setupCommand = new SetupCommand("", plugin);
        commands.add(new EnableCommand(Constants.enableCommand, plugin));
        commands.add(new DisableCommand(Constants.disableCommand, plugin));
        commands.add(new CancelCommand(Constants.cancelCommand, plugin));
        commands.add(new ReloadCommand(Constants.reloadCommand, plugin));
        commands.add(new ReportCommand(Constants.reportCommand, plugin));
        commands.add(new HelpCommand(Constants.helpCommand, plugin, commands));
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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String cmdLabel, @NotNull String[] args) {
        if(!commandSender.hasPermission("2fa.use")) {
            plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
            return false;
        }

        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if(plugin.getAuthHandler() == null) {
                plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SOMETHING_WENT_WRONG);
                return false;
            } else if(plugin.getAuthHandler().getAuthState(player.getUniqueId()) == null) {
                plugin.getAuthHandler().playerJoin(player.getUniqueId());
            }

            // If we don't receive an argument we want to either execute the help command if they have 2fa setup, or execute the setup (enable) command if they don't have it setup.
            if(args.length == 0) {
                Command subCommand = (plugin.getAuthHandler().is2FAEnabled(player.getUniqueId()) ? getCommand(Constants.helpCommand) : getCommand(Constants.enableCommand));
                if(subCommand != null) {
                    subCommand.execute(commandSender, args);
                    return true;
                }
            // If we received an argument, we want to try to authenticate the user
            } else {
                if(plugin.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.PENDING_LOGIN) {
                    loginCommand.execute(player, args);
                    return false;
                } else if(plugin.getAuthHandler().isPendingSetup(player.getUniqueId())) {
                    Command subCommand = getCommand(args[0]);
                    if(subCommand instanceof CancelCommand) subCommand.execute(player, args);
                    else setupCommand.execute(player, args);
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command cmd, String cmdLabel, String[] args) {
        if(!commandSender.hasPermission("2fa.use"))
            return null;

        // if we don't have any arguments we want to return a list of all sub commands the sender has permissions to
        if(args.length == 0)
            return commands.stream().filter(subCommand -> subCommand.hasPermissions(commandSender)).map(Command::getName).collect(Collectors.toList());

        Command subCommand = getCommand(args[0]);

        // If we don't have a subCommand matching the given argument, we want to return a list of all sub commands the sender has permissions to
        // and their name/one of their aliases contains the given argument value.
        // Example: if the input is "dis" we want to return the list: [disable] if the sender has permissions for this command
        if(subCommand == null) {

            return commands.stream()
                    // Filtering only the commands the sender has permissions to
                    .filter(subCmd -> subCmd.hasPermissions(commandSender))

                    // Mapping only commands that their name/one of their aliases match the argument
                    .map(subCmd -> {
                        if(subCmd.getName().toLowerCase().contains(args[0].toLowerCase()))
                            return subCmd.getName();

                        List<String> aliases = Arrays.stream(subCmd.getAliases())
                                .filter(subCmdAlias -> subCmdAlias.toLowerCase().contains(args[0].toLowerCase()))
                                .collect(Collectors.toList());

                        if(aliases.size() != 0)
                            return aliases.get(0);

                        return "";
                    })
                    // Collecting the result
                    .collect(Collectors.toList());
        }

        // If we have a matching subCommand, we want to trim down the arguments array from the first argument, and then call the subCommand #onTabComplete to handle the event
        String[] arguments = new String[args.length-1];
        System.arraycopy(args, 1, arguments, 0, arguments.length);

        return subCommand.onTabComplete(commandSender, arguments);
    }
}