package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private final TwoFactorAuthentication main;
    private YamlConfiguration config;
    private File file;

    public MessageHandler(TwoFactorAuthentication main) {
        this.main = main;

        loadConfiguration();
    }

    @Override
    protected void sendRaw(Object player, final String message) {
        if(player instanceof CommandSender)
            ((CommandSender)player).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void loadConfiguration() {
        if(!main.getDataFolder().exists())
            main.getDataFolder().mkdirs();

        this.file = new File(main.getDataFolder(), super.messagesFileName);

        if(!this.file.exists()) {
            try { this.file.createNewFile(); } catch (IOException exception) { exception.printStackTrace(); }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);

        for(TwoFAMessages message : TwoFAMessages.values()) {
            if(!this.config.contains(message.name())) {
                this.config.set(message.name(), message.getMessage());
            } else {
                message.setMessage(this.config.getString(message.name()));
            }
        }

        saveConfiguration();
    }

    @Override
    public void saveConfiguration() {
        try { this.config.save(this.file); } catch (IOException exception) { exception.printStackTrace(); }
    }
}