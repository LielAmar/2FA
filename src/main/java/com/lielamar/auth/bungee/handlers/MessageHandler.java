package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.shared.utils.color.ColorUtils;
import com.lielamar.auth.bungee.TwoFactorAuthentication;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private final TwoFactorAuthentication plugin;
    private Configuration config;
    private File file;

    public MessageHandler(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        this.reload();
    }

    @Override
    protected void sendRaw(final Object player, final String message) {
        if (player instanceof ProxiedPlayer) {
            ((ProxiedPlayer) player).sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(ColorUtils.translateAlternateColorCodes('&', message)));
        }
    }

    @Override
    public void reload() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdirs();
        }

        this.file = new File(this.plugin.getDataFolder(), super.messagesFileName);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);

            for (TwoFAMessages message : TwoFAMessages.values()) {
                if (!this.config.contains(message.name())) {
                    this.config.set(message.name(), message.getMessage());
                } else {
                    message.setMessage(this.config.getString(message.name()));
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        saveConfiguration();
    }

    @Override
    public void saveConfiguration() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
