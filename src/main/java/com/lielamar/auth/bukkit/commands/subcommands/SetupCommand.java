package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.bukkit.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand extends Command {

    private final Main main;
    public SetupCommand(String name, Main main) {
        super(name);
        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public String[] getPermissions() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(args.length == 0) {
            main.getMessageHandler().sendMessage(player, "&cUsage: /2fa <code>");
        } else {
            try {
                StringBuilder code = new StringBuilder();
                for(String arg : args)
                    code.append(arg);

                boolean approved = main.getAuthHandler().approveKey(player.getUniqueId(), Integer.parseInt(code.toString()));
                if(approved) {
                    main.getMessageHandler().sendMessage(player, "&aYou have successfully setup two-factor authentication");
                } else {
                    main.getMessageHandler().sendMessage(player, "&cIncorrect code, please try again");
                }
            } catch (Exception e) {
                main.getMessageHandler().sendMessage(player, "&cThe code you entered was not valid, please try again");
            }
        }
    }
}
