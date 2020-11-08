package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.shared.storage.StorageType;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandler extends com.lielamar.auth.shared.handlers.ConfigHandler {

    private final Main main;
    public ConfigHandler(Main main) {
        this.main = main;

        reload();
    }

    @Override
    public void reload() {
        FileConfiguration config = main.getConfig();
        if(main.getConfig() == null)
            main.saveDefaultConfig();

        this.serverName = config.getString("Server Name");
        this.storageType = StorageType.valueOf(config.getString("Storage Type"));
        this.requireOnIPChange = config.getBoolean("Require When.IP Changes");
        this.requireOnEveryLogin = config.getBoolean("Require When.Every Login");
        this.advice2FA = config.getBoolean("Advise");
        this.disableCommands = config.getBoolean("Disable Commands");
        this.whitelistedCommands = config.getStringList("Whitelisted Commands");
        this.blacklistedCommands = config.getStringList("Blacklisted Commands");
    }
}
