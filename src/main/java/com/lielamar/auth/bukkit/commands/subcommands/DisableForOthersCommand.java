package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import com.lielamar.lielsutils.bukkit.commands.SuperCommand;
import com.lielamar.lielsutils.bukkit.commands.TabOptionsBuilder;
import com.lielamar.lielsutils.exceptions.UUIDNotFoundException;
import com.lielamar.lielsutils.groups.Pair;
import com.lielamar.lielsutils.uuid.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class DisableForOthersCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public DisableForOthersCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.disableForOthersCommand.getA(), Constants.disableForOthersCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] targets) {
        for (String target : targets) {
            Player targetPlayer = Bukkit.getPlayer(target);

            if (targetPlayer != null) {
                UUID targetUUID = targetPlayer.getUniqueId();
                this.reset2FA(commandSender, target, targetUUID);
            } else {
                UUIDUtils.fetchUUIDFromMojang(target)
                        .exceptionally((exception) -> {
                            if (exception.getCause() instanceof UUIDNotFoundException) {
                                this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.PLAYER_NOT_FOUND,
                                        new Pair<>("%name%", target));
                            }
                            return null;
                        })
                        .thenAccept((uuid) -> this.reset2FA(commandSender, target, uuid));
            }
        }

        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return new TabOptionsBuilder().players().build(args);
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.parent.noPermissionEvent(commandSender);
    }

    @Override
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_DISABLE_OTHERS_COMMAND.getMessage());
    }

    private void reset2FA(CommandSender commandSender, String target, UUID targetUUID) {
        if (this.plugin.getAuthHandler().is2FAEnabled(targetUUID)) {
            this.plugin.getAuthHandler().resetKey(targetUUID);
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.RESET_FOR, new Pair<>("%name%", target));
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.YOUR_2FA_WAS_RESET);
        } else {
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.PLAYER_NOT_SETUP, new Pair<>("%name%", target));
        }
    }
}
