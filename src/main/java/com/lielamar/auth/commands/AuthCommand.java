package com.lielamar.auth.commands;

import com.lielamar.auth.utils.AuthUtils;
import com.lielamar.auth.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;

public class AuthCommand implements CommandExecutor {

    private final Main main;

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

            // If the amount of arguments is 0 we want to either set up 2fa/send the message to authenticate
            // If the amount of arguments is greater than 0, we want to authenticate player/remove their key
            if(args.length == 0) {
                return this.main.getAuthDatabaseManager().setup2FA(player);
            } else {
                String codeRaw = Arrays.toString(args);

                try {
                    int code = Integer.parseInt(codeRaw);
                    return this.main.getAuthDatabaseManager().authenticatePlayer(player, code);
                } catch(IllegalArgumentException e) {
                    if(codeRaw.equalsIgnoreCase("remove") || codeRaw.equalsIgnoreCase("reset"))
                        return this.main.getAuthDatabaseManager().removePlayerSecretKey(player, player.getUniqueId());
                }
            }
        }
        return false;
    }
}