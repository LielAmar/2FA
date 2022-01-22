package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelCommand extends Command {

    private final TwoFactorAuthentication main;

    public CancelCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "cancel", "stop" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.cancel" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_CANCEL_COMMAND.getMessage());
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] targets) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            if(!(commandSender instanceof Player)) {
                main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
            } else {
                Player player = (Player) commandSender;

                if(main.getAuthHandler().isPendingSetup(player.getUniqueId())) {
                    main.getAuthHandler().cancelKey(player.getUniqueId());
                    main.getAuthHandler().removeQRItem(player);
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.CANCELED_SETUP);
                } else {
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.NOT_IN_SETUP_MODE);
                }
            }
        }
    }
}