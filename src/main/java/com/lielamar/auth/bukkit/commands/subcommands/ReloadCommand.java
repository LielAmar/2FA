package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.bukkit.commands.Command;
import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReloadCommand extends Command {

    private final Main main;
    public ReloadCommand(String name, Main main) {
        super(name);
        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.reload" };
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;

        if(!hasPermissions(player)) {
            main.getMessageHandler().sendMessage(player, "&cYou do not have permission to run this command");
        } else {
            Map<UUID, AuthHandler.AuthState> states = new HashMap<>();
            for(Player pl : Bukkit.getOnlinePlayers()) {
                if(main.getAuthHandler().getAuthState(pl.getUniqueId()) != null)
                    states.put(pl.getUniqueId(), main.getAuthHandler().getAuthState(pl.getUniqueId()));
            }

            main.reloadConfig();
            main.setupAuth();

            for(UUID uuid : states.keySet())
                main.getAuthHandler().changeState(uuid, states.get(uuid));

            main.getMessageHandler().sendMessage(player, "&aConfig was reloaded");
        }
    }
}