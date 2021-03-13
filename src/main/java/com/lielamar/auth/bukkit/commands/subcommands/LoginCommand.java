package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.events.PlayerFailedAuthenticationEvent;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand extends Command {

    private final TwoFactorAuthentication main;

    public LoginCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.use" };
    }

    @Override
    public String getDescription() {
        return "";
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

                try {
                    StringBuilder code = new StringBuilder();

                    for(String arg : args)
                        code.append(arg);

                    boolean isValid = main.getAuthHandler().validateKey(player.getUniqueId(), Integer.valueOf(code.toString()));
                    if(isValid) {
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SUCCESSFULLY_AUTHENTICATED);
                    } else {
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INCORRECT_CODE);
                        callFailEvent(player);
                    }
                } catch (Exception exception) {
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INVALID_CODE);
                    callFailEvent(player);
                }
            }
        }
    }

    private void callFailEvent(Player player) {
        PlayerFailedAuthenticationEvent event = new PlayerFailedAuthenticationEvent(player, main.getAuthHandler().increaseFailedAttempts(player.getUniqueId(), 1));
        Bukkit.getPluginManager().callEvent(event);
    }
}
