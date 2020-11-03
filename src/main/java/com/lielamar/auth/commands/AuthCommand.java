package com.lielamar.auth.commands;

import com.lielamar.auth.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthCommand implements CommandExecutor {

    private Main main;

    public AuthCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("2fa")) {
            if(!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "You must be a player in order to use 2 Factor Authentication!");
                return false;
            }

            Player player = (Player)cs;

            if(args.length == 0) {
                if(!main.getAuthManager().hasAuthentication(player)) {
                    if(!player.hasPermission("2fa.setup")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to set up 2 Factor Authentication!");
                        return false;
                    }

                    // TODO: setup
                } else {
                    // TODO: Send help message of the command
                }
            } else {
                if(!main.getAuthManager().hasAuthentication(player)) {
                    player.sendMessage(ChatColor.RED + "You don't have 2 Factor Authentication set up! Use /2fa to set it up first!");
                    return false;
                }

                StringBuilder codeBuilder = new StringBuilder();
                for(String s : args)
                    codeBuilder.append(s);

                String code = codeBuilder.toString();
                boolean auth = main.getAuthManager().authenticate(player, code);

                if(!auth) {
                    player.kickPlayer("Wrong code");
                    return false;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Authenticated!");
                }
            }
        }
        return false;
    }
}
