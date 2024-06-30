package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.utils.cmd.StandaloneCommand;
import com.lielamar.auth.bukkit.utils.cmd.SuperCommand;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetupCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public SetupCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.setupCommand.getA(), Constants.setupCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.MUST_BE_A_PLAYER);
            return false;
        }

        Player player = (Player) commandSender;

        if (args.length == 0) {
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.USAGE);
            return false;
        }

        if (!this.plugin.getAuthHandler().isPendingSetup(player.getUniqueId())) {
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.DIDNT_SETUP_YET);
            return false;
        }

        StringBuilder code = new StringBuilder();

        for (String arg : args) {
            code.append(arg);
        }

        try {
            boolean approved = this.plugin.getAuthHandler().approveKey(player.getUniqueId(), code.toString());

            if (approved) {
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SUCCESSFULLY_SETUP);
            } else {
                this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INCORRECT_CODE);
            }
        } catch (NumberFormatException exception) {
            this.plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INVALID_CODE);
        }
        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.parent.noPermissionEvent(commandSender);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
