package com.lielamar.auth.bukkit.handlers;

import com.lielamar.lielsutils.files.FileManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private static boolean PLACEHOLDER_API_ENABLED;

    private final FileManager.Config config;

    public MessageHandler(FileManager fileManager) {
        PLACEHOLDER_API_ENABLED = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        this.config = fileManager.getConfig(super.messagesFileName);

        this.reload();
    }

    @Override
    protected void sendRaw(Object sender, String message) {
        if(sender instanceof CommandSender) {
            if(PLACEHOLDER_API_ENABLED && sender instanceof Player)
                message = PlaceholderAPI.setPlaceholders((Player) sender, message);

            ((CommandSender)sender).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public void reload() {
        this.config.reloadConfig();

        for(TwoFAMessages message : TwoFAMessages.values()) {
            if(!this.config.contains(message.name()))
                this.config.set(message.name(), message.getMessage());
            else
                message.setMessage(this.config.getString(message.name()));
        }

        this.saveConfiguration();
    }

    @Override
    public void saveConfiguration() {
        this.config.saveConfig();
    }
}