package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReloadCommand extends Command {

    private final TwoFactorAuthentication main;

    public ReloadCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    public String[] getAliases() {
        return new String[] { "rl" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.reload" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_RELOAD_COMMAND.getMessage());
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            Map<UUID, AuthHandler.AuthState> states = new HashMap<>();
            AuthHandler.AuthState state;

            for(Player pl : Bukkit.getOnlinePlayers()) {
                state = main.getAuthHandler().getAuthState(pl.getUniqueId());
                if(state != null) states.put(pl.getUniqueId(), state);
            }

            main.reloadConfig();
            main.getMessageHandler().loadConfiguration();
            main.setupAuth();

            for(UUID uuid : states.keySet())
                main.getAuthHandler().changeState(uuid, states.get(uuid));

            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.RELOADED_CONFIG);
        }
    }
}