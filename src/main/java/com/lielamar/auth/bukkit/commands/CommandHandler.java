package com.lielamar.auth.bukkit.commands;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.bukkit.commands.subcommands.*;
import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CommandHandler implements CommandExecutor {

    private final Main main;
    private final Set<Command> commands;
    private Command loginCommand, setupCommand;

    public static final String mainCommand = "2fa";
    private static final String enableCommand = "enable";
    private static final String disableCommand = "disable";
    private static final String reloadCommand = "reload";
    private static final String helpCommand = "help";

    public CommandHandler(Main main) {
        this.main = main;
        this.main.getCommand(mainCommand).setExecutor(this);

        this.commands = new HashSet<>();
        this.setupCommands();
    }

    /**
     * Sets up all of the sub commands of /2fa
     */
    private void setupCommands() {
        loginCommand = new LoginCommand("", main);
        setupCommand = new SetupCommand("", main);
        commands.add(new EnableCommand(enableCommand, main));
        commands.add(new DisableCommand(disableCommand, main));
        commands.add(new ReloadCommand(reloadCommand, main));
        commands.add(new HelpCommand(helpCommand));
    }

    /**
     * Returns a {@link com.lielamar.auth.bukkit.commands.Command} object related to the given name
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
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmd, String cmdLabel, String[] args) {
        if(!(cs instanceof Player)) {
            cs.sendMessage(main.getMessageHandler().getMessage("&cThis command must be ran as a player"));
            return false;
        }

        Player player = (Player)cs;

        if(main.getAuthHandler().getAuthState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_LOGIN)) {
            loginCommand.execute(player, args);
        } else if(main.getAuthHandler().getAuthState(player.getUniqueId()).equals(AuthHandler.AuthState.PENDING_SETUP)) {
            setupCommand.execute(player, args);
        } else {
            if(args.length == 0) {
                Command subCommand = getCommand(helpCommand);
                if(subCommand != null) subCommand.execute(player, args);
            } else {
                Command subCommand = getCommand(args[0]);
                if(subCommand == null) subCommand = getCommand(helpCommand);

                String[] arguments = new String[args.length-1];
                System.arraycopy(args, 1, arguments, 0, arguments.length);
                subCommand.execute(player, arguments);
            }
        }
        return false;
    }
}