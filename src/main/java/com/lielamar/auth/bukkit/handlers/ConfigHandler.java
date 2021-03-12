package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class ConfigHandler extends com.lielamar.auth.shared.handlers.ConfigHandler {

    private final TwoFactorAuthentication main;

    public ConfigHandler(TwoFactorAuthentication main) {
        this.main = main;

        reload();
    }

    @Override
    public void reload() {
        FileConfiguration config = main.getConfig();

        this.amountOfReservedMaps = config.getInt("Reserved Maps");
        this.mapIDs = new int[amountOfReservedMaps];
        this.serverName = config.getString("Server Name");
        this.hashType = config.getString("IP Hash");
        this.storageType = StorageType.valueOf(config.getString("Storage Type", "JSON").toUpperCase());
        this.requireOnIPChange = config.getBoolean("Require When.IP Changes");
        this.requireOnEveryLogin = config.getBoolean("Require When.Every Login");
        this.advice2FA = config.getBoolean("Advise");

        ConfigurationSection disabledEventsSection = config.getConfigurationSection("Disabled Events");
        try {
            if(disabledEventsSection != null) {
                for(String key : disabledEventsSection.getKeys(false))
                    this.disabledEvents.put(ShorterEvents.valueOf(key.toUpperCase().replaceAll(" ", "_")).getMatchingEvent(), disabledEventsSection.getBoolean(key));
            }
        } catch(IllegalArgumentException exception) {
            exception.printStackTrace();
            System.out.println("[2FA] The plugin detected that your configuration is having some wrong Disabled Events. Please re-check your configuration and make sure the Disabled Events names are correct!");
        }

        this.whitelistedCommands = config.getStringList("Whitelisted Commands");
        this.blacklistedCommands = config.getStringList("Blacklisted Commands");

        if(!config.contains("Map IDs")) {
            config.set("Map IDs", new ArrayList<>());
            main.saveConfig();
        }

        int index = 0;
        for(int i : config.getIntegerList("Map IDs"))
            mapIDs[index++] = i;
    }
}
