package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.bungee.TwoFactorAuthPlugin;
import com.lielamar.auth.core.config.AbstractConfigHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Singleton
public final class BungeeConfigHandler extends AbstractConfigHandler {
    private final TwoFactorAuthPlugin plugin;

    private boolean disableCommands = true;
    private boolean disableChat = true;
    private boolean disableServerSwitch = true;

    @Inject
    public BungeeConfigHandler(final @NotNull TwoFactorAuthPlugin plugin) {
        this.plugin = plugin;

        reload();
    }

    @Override
    public void reload() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        File file = new File(this.plugin.getDataFolder(), "bungee-config.yml");
        if (!file.exists()) {
            try ( InputStream in = this.plugin.getResourceAsStream("bungee-config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            if (!config.contains("disabled-events.commands")) {
                config.set("disabled-events.commands", this.disableCommands);
            } else {
                this.disableCommands = config.getBoolean("disabled-events.commands");
            }

            if (!config.contains("disabled-events.chat")) {
                config.set("disabled-events.chat", this.disableChat);
            } else {
                this.disableChat = config.getBoolean("disabled-events.chat");
            }

            if (!config.contains("disabled-events.server-switch")) {
                config.set("disabled-events.server-switch", this.disableServerSwitch);
            } else {
                this.disableServerSwitch = config.getBoolean("disabled-events.server-switch");
            }

            if (!config.contains("whitelisted-commands")) {
                config.set("whitelisted-commands", this.whitelistedCommands);
            } else {
                super.whitelistedCommands = config.getStringList("whitelisted-commands");
            }

            if (!config.contains("blacklisted-commands")) {
                config.set("blacklisted-commands", this.blacklistedCommands);
            } else {
                super.blacklistedCommands = config.getStringList("blacklisted-commands");
            }

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean isDisableCommands() {
        return this.disableCommands;
    }

    public boolean isDisableChat() {
        return this.disableChat;
    }

    public boolean isDisableServerSwitch() {
        return this.disableServerSwitch;
    }

    public String getJSONConfigPath() {
        return plugin.getDataFolder().getAbsolutePath();
    }
}
