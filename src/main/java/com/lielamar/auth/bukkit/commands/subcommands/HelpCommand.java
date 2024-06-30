package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.utils.cmd.StandaloneCommand;
import com.lielamar.auth.bukkit.utils.cmd.SuperCommand;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.auth.shared.utils.groups.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public HelpCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.helpCommand.getA(), Constants.helpCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        this.plugin.getMessageHandler().sendMessage(commandSender, false, MessageHandler.TwoFAMessages.HELP_HEADER);

        Arrays.stream(this.parent.getSubCommands()).forEach(cmd -> {
            if (cmd.getPermission() == null || commandSender.hasPermission(cmd.getPermission())) {
                this.plugin.getMessageHandler().sendMessage(commandSender, false, MessageHandler.TwoFAMessages.HELP_COMMAND,
                        new Pair<>("%command%", "/2FA " + cmd.getCommandName()),
                        new Pair<>("%description%", cmd.getDescription()),
                        new Pair<>("%aliases%", Arrays.toString(cmd.getAliases()))
                );
            }
        });

        this.plugin.getMessageHandler().sendMessage(commandSender, false, MessageHandler.TwoFAMessages.HELP_FOOTER);
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
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_HELP_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"assist"};
    }
}
