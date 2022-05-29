package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import com.lielamar.lielsutils.bukkit.commands.SuperCommand;
import com.lielamar.lielsutils.bukkit.commands.TabOptionsBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DisableCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    private final DisableForOthersCommand disableForOthersCommand;

    public DisableCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.disableCommand.getA(), Constants.disableCommand.getB());

        this.plugin = plugin;
        this.parent = parent;

        this.disableForOthersCommand = new DisableForOthersCommand(plugin, parent);
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] targets) {
        if (targets.length == 0) {
            if (!(commandSender instanceof Player)) {
                this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
                return false;
            }

            Player player = (Player) commandSender;

            if (this.plugin.getAuthHandler().is2FAEnabled(player.getUniqueId())) {
                this.plugin.getAuthHandler().resetKey(player.getUniqueId());
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.RESET_2FA);
            } else {
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.NOT_SETUP);
            }

            return false;
        }

        this.disableForOthersCommand.execute(commandSender, targets);
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
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_DISABLE_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"remove", "disable", "reset", "off", "deactivate", "false"};
    }
}
