package com.lielamar.auth.commands;

import com.lielamar.auth.Main;
import com.lielamar.auth.utils.AuthUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class AuthAdminCommand implements CommandExecutor {

    private final Main main;

    public AuthAdminCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("2faadmin")) {

            if(args.length == 0) {
                AuthUtils.sendHelpCommand(cs);
                return true;
            } else {
                String action = args[0];

                if(action.equalsIgnoreCase("reload")) {
                    if(!cs.hasPermission("2fa.reload")) {
                        cs.sendMessage(ChatColor.RED + "You don't have enough permissions to reload the 2FA plugin!");
                        return false;
                    }

                    this.main.reloadConfig();
                    this.main.setupAuth();
                    cs.sendMessage(ChatColor.GRAY + "Reloaded the " + ChatColor.AQUA + "2FA" + ChatColor.GRAY + " plugin!");
                    return true;
                } else if(action.equalsIgnoreCase("remove")) {
                    if(!cs.hasPermission("2fa.remove.others")) {
                        cs.sendMessage(ChatColor.RED + "You don't have enough permissions to remove 2FA of others!");
                        return false;
                    }

                    if(args.length < 2) {
                        AuthUtils.sendHelpCommand(cs);
                        return true;
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            UUID targetUUID = AuthUtils.fetchUUID(args[1]);

                            if(targetUUID == null)
                                cs.sendMessage(ChatColor.RED + "The player " + args[1] + " could not be found!");
                            else
                                main.getAuthDatabaseManager().removePlayerSecretKey(cs, targetUUID);
                        }
                    }.runTaskAsynchronously(this.main);
                    return true;
                }
            }
        }
        return false;
    }
}
