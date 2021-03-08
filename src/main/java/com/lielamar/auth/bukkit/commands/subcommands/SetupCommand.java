package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand extends Command {

    private final TwoFactorAuthentication main;

    public SetupCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.enable" };
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

                if(args.length == 0) {
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.USAGE);
                } else {
                    try {
                        StringBuilder code = new StringBuilder();

                        for(String arg : args)
                            code.append(arg);

                        boolean approved = main.getAuthHandler().approveKey(player.getUniqueId(), Integer.parseInt(code.toString()));
                        if(approved) {
                            main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SUCCESSFULLY_SETUP);
                        } else {
                            main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INCORRECT_CODE);
                        }
                    } catch (Exception e) {
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INVALID_CODE);
                    }
                }
            }
        }
    }
}
