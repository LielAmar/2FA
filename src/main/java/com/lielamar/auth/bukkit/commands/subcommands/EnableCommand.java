package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableCommand extends Command {

    private final TwoFactorAuthentication main;

    public EnableCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "add", "enable", "setup", "on", "activate", "true" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.use" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_ENABLE_COMMAND.getMessage());
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            if(!(commandSender instanceof Player)) {
                main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
            } else {
                Player player = (Player) commandSender;

                if(main.getAuthHandler().getAuthState(player.getUniqueId()) == AuthHandler.AuthState.DISABLED) {
                    main.getAuthHandler().createKey(player.getUniqueId());
                } else {
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.ALREADY_SETUP);
                }
            }
        }
    }
}