package com.lielamar.auth.bukkit.commands.subcommands;

import java.util.UUID;

import com.lielamar.lielsutils.commands.Command;
import com.lielamar.lielsutils.modules.Pair;
import org.bukkit.entity.Player;
import com.lielamar.auth.shared.utils.AuthUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.bukkit.TwoFactorAuthentication;

public class DisableForOthersCommand extends Command {

    private final TwoFactorAuthentication main;

    public DisableForOthersCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "remove", "disable", "reset", "off", "deactivate", "false" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.remove.others" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_DISABLE_OTHERS_COMMAND.getMessage());
    }

    @Override
    public void execute(CommandSender commandSender, String[] targets) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            Bukkit.getScheduler().runTask(main, () -> {
                for(String target : targets) {
                    Player targetPlayer = Bukkit.getPlayer(target);
                    UUID targetUUID;

                    if(targetPlayer != null)
                        targetUUID = targetPlayer.getUniqueId();
                    else
                        targetUUID = AuthUtils.fetchUUID(target);

                    if(targetUUID == null) {
                        main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.PLAYER_NOT_FOUND, new Pair<>("%name%", target));
                    } else {
                        if(main.getAuthHandler().is2FAEnabled(targetUUID)) {
                            main.getAuthHandler().resetKey(targetUUID);
                            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.RESET_FOR, new Pair<>("%name%", target));
                        } else {
                            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.PLAYER_NOT_SETUP, new Pair<>("%name%", target));
                        }
                    }
                }
            });
        }
    }
}