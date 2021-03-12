package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import com.lielamar.lielsutils.modules.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Set;

public class HelpCommand extends Command {

    private final TwoFactorAuthentication main;
    private final Set<Command> commands;

    public HelpCommand(String name, TwoFactorAuthentication main, Set<Command> commands) {
        super(name);

        this.main = main;
        this.commands = commands;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "assist" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.help" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_HELP_COMMAND.getMessage());
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            main.getMessageHandler().sendMessage(commandSender, false, MessageHandler.TwoFAMessages.HELP_HEADER);

            this.commands.forEach(cmd -> {
                if(cmd.hasPermissions(commandSender)) {
                    main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.HELP_COMMAND,
                            new Pair<>("%command%", "/2FA " + cmd.getName()),
                            new Pair<>("%description%", cmd.getDescription()),
                            new Pair<>("%aliases%", Arrays.toString(cmd.getAliases()))
                    );
                }
            });

            main.getMessageHandler().sendMessage(commandSender, false, MessageHandler.TwoFAMessages.HELP_FOOTER);
        }
    }
}
