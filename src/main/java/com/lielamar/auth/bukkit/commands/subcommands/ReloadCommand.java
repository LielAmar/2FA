package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.bukkit.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            main.reloadConfig();
            main.setupAuth();
            main.getMessageHandler().sendMessage(player, "&aConfig was reloaded");
        }
    }
}