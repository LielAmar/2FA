package com.lielamar.auth.velocity.handlers;

import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigHandler extends com.lielamar.auth.shared.handlers.ConfigHandler {

    private final TwoFactorAuthentication plugin;

    private boolean disableCommands = true;
    private boolean disableChat = true;
    private boolean disableServerSwitch = true;

    public ConfigHandler(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        this.reload();
    }

    @Override
    public void reload() {
        if(!this.plugin.getDataDirectory().toFile().exists())
            this.plugin.getDataDirectory().toFile().mkdir();

        File file = new File(this.plugin.getDataDirectory().toFile(), "config.toml");

        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if(!file.exists()) {
            try(InputStream input = getClass().getResourceAsStream("/velocityconfig.toml")) {
                if(input != null)
                    Files.copy(input, file.toPath());
                else
                    file.createNewFile();
            } catch(IOException exception) {
                exception.printStackTrace();
                return;
            }
        }


        Toml config = new Toml().read(file);

        Toml disabledEvents = config.getTable("disabled.events");
        Toml commands = config.getTable("commands");

        this.disableCommands = disabledEvents.getBoolean("commands", true);
        this.disableChat = disabledEvents.getBoolean("chat", true);
        this.disableServerSwitch = disabledEvents.getBoolean("server-switch", true);

        this.whitelistedCommands.addAll(commands.getList("whitelisted"));
        this.blacklistedCommands.addAll(commands.getList("blacklisted"));
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
}