package com.lielamar.auth.bukkit.handlers;

import com.lielamar.lielsutils.files.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private final FileManager.Config config;

    public MessageHandler(FileManager fileManager) {
        this.config = fileManager.getConfig(super.messagesFileName);

        this.reload();
    }

    @Override
    protected void sendRaw(Object player, final String message) {
        if(player instanceof CommandSender)
            ((CommandSender)player).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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