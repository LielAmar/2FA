package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import com.lielamar.lielsutils.bukkit.commands.SuperCommand;
import com.lielamar.lielsutils.bukkit.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReloadCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public ReloadCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.reloadCommand.getA(), Constants.reloadCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        Map<UUID, AuthHandler.AuthState> states = new HashMap<>();
        
        Bukkit.getOnlinePlayers().forEach(players -> {
            states.put(players.getUniqueId(), 
                    this.plugin.getAuthHandler().getAuthState(players.getUniqueId()));
        });

        FileManager.Config config = this.plugin.getFileManager().getConfig("config");
        if (config != null) {
            config.reloadConfig();
        }

        this.plugin.getMessageHandler().reload();
        this.plugin.setupAuth();
        
        states.keySet().forEach(uuid
                -> this.plugin.getAuthHandler().changeState(uuid, states.get(uuid)));

        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.RELOADED_CONFIG);
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
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_RELOAD_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rl"};
    }
}
