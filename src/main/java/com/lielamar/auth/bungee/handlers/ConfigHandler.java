package com.lielamar.auth.bungee.handlers;

import com.lielamar.auth.bungee.Main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigHandler extends com.lielamar.auth.shared.handlers.ConfigHandler {

    private final String configName = "config.yml";

    private final Main main;

    private boolean disableServerSwitch;

    public ConfigHandler(Main main) {
        this.main = main;

        reload();
    }

    @Override
    public void reload() {
        if(!main.getDataFolder().exists())
            main.getDataFolder().mkdir();

        File file = new File(main.getDataFolder(), configName);

        if(!file.exists()) {
            try(InputStream in = main.getResourceAsStream("bungeeconfig.yml")) {
                Files.copy(in, file.toPath());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            this.disableCommands = config.getBoolean("Disable Commands");
            this.disableServerSwitch = config.getBoolean("Disable Server Switch");
            this.whitelistedCommands = config.getStringList("Whitelisted Commands");
            this.blacklistedCommands = config.getStringList("Blacklisted Commands");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDisableServerSwitch() {
        return this.disableServerSwitch;
    }
}
