package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.bukkit.commands.Command;
import com.lielamar.auth.shared.utils.AuthUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DisableCommand extends Command {

    private final Main main;
    public DisableCommand(String name, Main main) {
        super(name);
        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "remove", "disable", "reset", "off", "deactivate", "false" };
    }

    @Override
    public String[] getPermissions() {
        return null;
    }

    @Override
    public void execute(CommandSender commandSender, String[] targets) {
        Player player = (Player) commandSender;

        if(targets.length == 0) {
            if(!player.hasPermission("2fa.remove")) {
                main.getMessageHandler().sendMessage(player, "&cYou do not have permission to run this command");
            } else {
                if(main.getAuthHandler().is2FAEnabled(player.getUniqueId())) {
                    main.getAuthHandler().resetKey(player.getUniqueId());
                    main.getMessageHandler().sendMessage(player, "&aYour 2FA has been reset");
                } else {
                    main.getMessageHandler().sendMessage(player, "&cYou are not setup with 2FA");
                }
            }
        } else {
            if(!player.hasPermission("2fa.remove.others")) {
                main.getMessageHandler().sendMessage(player, "&cYou do not have permission to run this command");
            } else {
                Bukkit.getScheduler().runTask(main, () -> {
                    for(String target : targets) {
                        Player targetPlayer = Bukkit.getPlayer(target);
                        UUID targetUUID;

                        if(targetPlayer != null)
                            targetUUID = player.getUniqueId();
                        else
                            targetUUID = AuthUtils.fetchUUID(target);

                        if(targetUUID == null) {
                            main.getMessageHandler().sendMessage(player, "&cPlayer %name% could not be found".replaceAll("%name%", target));
                        } else {
                            if(main.getAuthHandler().is2FAEnabled(targetUUID)) {
                                main.getAuthHandler().resetKey(targetUUID);
                                main.getMessageHandler().sendMessage(player, "&a%name%'s 2FA has been reset".replaceAll("%name%", target));
                            } else {
                                main.getMessageHandler().sendMessage(player, "&c%name% is not setup with 2FA".replaceAll("%name%", target));
                            }
                        }
                    }
                });
            }
        }
    }
}