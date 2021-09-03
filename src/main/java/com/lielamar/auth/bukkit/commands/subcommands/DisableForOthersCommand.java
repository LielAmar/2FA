package com.lielamar.auth.bukkit.commands.subcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Executes the Disable command for other players
     *
     * It checks for permissions and loops over the targets, from there, it has 2 ways it can go:
     *   1. The current player in the loop is not null (meaning they're online).
     *      In this case, everything would run on Main Thread (synchronized) since we have access to their UUID directly from the server.
     *      With this approach, we can also safely call PlayerStateChangeEvent eventually (after resetting the key and changing the player's state)
     *      since it would run on Main Thread.
     *   2. The current player in the loop is null (meaning they're offline).
     *      In this case, everything would run on a separate thread (asynchronized) since we need to fetch Mojang's api to retrieve their UUID if they exist.
     *      If they do exist, we want to reset their key. When resetting their key, the plugin will try to change the player's state, but since we know
     *      for a fact the player is offline, it would not call PlayerStateChangeEvent, avoiding the IllegalStateException caused by calling events not in Main Thread.
     *
     * TL;DR
     * this function can either be sync if the player is online (and then call PlayerStateChangeEvent) or run async if they're offline (and then not call the event)
     */
    @Override
    public void execute(CommandSender commandSender, String[] targets) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            for(String target : targets) {
                Player targetPlayer = Bukkit.getPlayer(target);

                if(targetPlayer != null) {
                    UUID targetUUID = targetPlayer.getUniqueId();
                    reset2FA(commandSender, target, targetUUID);
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                        UUID targetUUID = AuthUtils.fetchUUID(target);

                        if(targetUUID == null)
                            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.PLAYER_NOT_FOUND, new Pair<>("%name%", target));
                        else
                            reset2FA(commandSender, target, targetUUID);
                    });
                }
            }
        }
    }

    private void reset2FA(CommandSender commandSender, String target, UUID targetUUID) {
        if(main.getAuthHandler().is2FAEnabled(targetUUID)) {
            main.getAuthHandler().resetKey(targetUUID);
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.RESET_FOR, new Pair<>("%name%", target));
        } else {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.PLAYER_NOT_SETUP, new Pair<>("%name%", target));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args) {
        if(!hasPermissions(commandSender))
            return new ArrayList<>();

        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}