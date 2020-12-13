package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.storage.StorageType;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandler extends com.lielamar.auth.shared.handlers.ConfigHandler {

    private final TwoFactorAuthentication main;
    public ConfigHandler(TwoFactorAuthentication main) {
        this.main = main;

        reload();
    }

    @Override
    public void reload() {
        FileConfiguration config = main.getConfig();

        this.serverName = config.getString("Server Name");
        this.hashType = config.getString("IP Hash");
        this.storageType = StorageType.valueOf(config.getString("Storage Type", "JSON").toUpperCase());
        this.requireOnIPChange = config.getBoolean("Require When.IP Changes");
        this.requireOnEveryLogin = config.getBoolean("Require When.Every Login");
        this.advice2FA = config.getBoolean("Advise");
        this.disableCommands = config.getBoolean("Disable Commands");
        this.whitelistedCommands = config.getStringList("Whitelisted Commands");
        this.blacklistedCommands = config.getStringList("Blacklisted Commands");
    }
}
