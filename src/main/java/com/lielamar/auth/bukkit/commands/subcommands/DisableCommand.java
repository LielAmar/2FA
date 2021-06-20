package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DisableCommand extends Command {

    private final TwoFactorAuthentication main;
    private final DisableForOthersCommand disableForOthersCommand;

    public DisableCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
        this.disableForOthersCommand = new DisableForOthersCommand(name, main);
    }

    @Override
    public String[] getAliases() {
        return new String[] { "remove", "disable", "reset", "off", "deactivate", "false" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.remove" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_DISABLE_COMMAND.getMessage());
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] targets) {
        if(targets.length == 0) {
            if(!hasPermissions(commandSender)) {
                main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
            } else {
                if(!(commandSender instanceof Player)) {
                    main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
                } else {
                    Player player = (Player) commandSender;

                    if(main.getAuthHandler().is2FAEnabled(player.getUniqueId())) {
                        main.getAuthHandler().resetKey(player.getUniqueId());
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.RESET_2FA);
                    } else {
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.NOT_SETUP);
                    }
                }
            }
        } else {
            disableForOthersCommand.execute(commandSender, targets);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args) {
        if(!hasPermissions(commandSender))
            return null;

        if(args.length != 0)
            return disableForOthersCommand.onTabComplete(commandSender, args);

        return null;
    }
}