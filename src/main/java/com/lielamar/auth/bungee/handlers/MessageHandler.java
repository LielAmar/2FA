package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.bungee.TwoFactorAuthentication;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private final String configName = "messages.yml";

    private final TwoFactorAuthentication main;
    private Configuration messagesConfig;

    public MessageHandler(TwoFactorAuthentication main) {
        this.main = main;

        loadConfiguration();
    }

    public String getPrefix() {
        String prefix = super.getPrefix();
        if(messagesConfig.contains("Prefix"))
            prefix = messagesConfig.getString("Prefix") + "&r ";

        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getMessage(String message) {
        message = messagesConfig.getString(message, message);
        return getPrefix() + ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(ProxiedPlayer player, String message) {
        message = getMessage(message);
        player.sendMessage(new TextComponent(message).toLegacyText());
    }

    public void loadConfiguration() {
        if(!main.getDataFolder().exists())
            main.getDataFolder().mkdir();

        File file = new File(main.getDataFolder(), configName);

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            messagesConfig = ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(file);

            if(!messagesConfig.contains("Prefix")) {
                messagesConfig.set("Prefix", "&7[&b2FA&7]");
            }

            for(String message : getDefaults()) {
                if(!messagesConfig.contains(message)) {
                    messagesConfig.set(message, message);
                }
            }

            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(messagesConfig, new File(main.getDataFolder(), configName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}