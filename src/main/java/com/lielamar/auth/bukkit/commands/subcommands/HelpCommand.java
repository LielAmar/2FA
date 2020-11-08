package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends Command {

    public HelpCommand(String name) {
        super(name);
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public String[] getPermissions() {
        return null;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;

        player.sendMessage(ChatColor.GRAY + "----- " + ChatColor.AQUA + "2FA" + ChatColor.GRAY + " -----");
        player.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/2fa remove" + ChatColor.GRAY + " - Disable your 2FA");
        player.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/2fa enable" + ChatColor.GRAY + " - Enable your 2FA");
        if(player.hasPermission("2fa.remove.others"))
            player.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/2fa remove <player>" + ChatColor.GRAY + " - Disable a player's 2FA");
        if(player.hasPermission("2fa.reload"))
            player.sendMessage(ChatColor.GRAY + "• " + ChatColor.AQUA + "/2fa reload" + ChatColor.GRAY + " - Reload the 2FA plugin");
        player.sendMessage(ChatColor.GRAY + "----- -------- -----");
    }
}
