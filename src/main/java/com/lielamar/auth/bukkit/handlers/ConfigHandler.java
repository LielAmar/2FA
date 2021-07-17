package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.storage.StorageMethod;
import org.bukkit.configuration.ConfigurationSection;
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

        if(!config.contains("qr-code-service"))
            config.set("qr-code-service", super.qrCodeURL);
        else
            super.qrCodeURL = config.getString("qr-code-service");

        if(!config.contains("check-for-updates"))
            config.set("check-for-updates", super.checkForUpdates);
        else
            super.checkForUpdates = config.getBoolean("check-for-updates");

        if(!config.contains("server-name"))
            config.set("server-name", super.serverName);
        else
            super.serverName = config.getString("server-name");


        if(!config.contains("advise-2fa"))
            config.set("advise-2fa", super.advise2FA);
        else
            super.advise2FA = config.getBoolean("advise-2fa");

        if(!config.contains("reserved-maps"))
            config.set("reserved-maps", super.reservedMaps);
        else
            super.reservedMaps = config.getInt("reserved-maps");

        if(!config.contains("map-ids"))
            config.set("map-ids", super.mapIDs);
        else
            super.mapIDs = new int[super.reservedMaps];

        int index = 0;
        for(int i : config.getIntegerList("map-ids"))
            super.mapIDs[index++] = i;

        if(!config.contains("ip-hash"))
            config.set("ip-hash", super.ipHashType);
        else
            super.ipHashType = config.getString("ip-hash");

        ConfigurationSection disabledEventsSection = config.getConfigurationSection("disabled-events");
        try {
            if(disabledEventsSection != null) {
                for(String key : disabledEventsSection.getKeys(false))
                    super.disabledEvents.put(ShorterEvents.valueOf(key.toUpperCase().replaceAll("-", "_")).getMatchingEvent(), disabledEventsSection.getBoolean(key));
            } else {
                config.createSection("disabled-events");
            }
        } catch(IllegalArgumentException exception) {
            exception.printStackTrace();
            System.out.println("[2FA] The plugin detected that your configuration is having some incompatible Events in the \"disabled-events\" section." +
                    "Please re-check your configuration and make sure the Disabled Events names are correct!");
        }

        if(!config.contains("whitelisted-commands"))
            config.set("whitelisted-commands", super.whitelistedCommands);
        else
            super.whitelistedCommands = config.getStringList("whitelisted-commands");

        if(!config.contains("blacklisted-commands"))
            config.set("blacklisted-commands", super.blacklistedCommands);
        else
            super.blacklistedCommands = config.getStringList("blacklisted-commands");

        if(!config.contains("require-when.ip-changes"))
            config.set("require-when.ip-changes", super.requireOnIPChange);
        else
            super.requireOnIPChange = config.getBoolean("require-when.ip-changes");

        if(!config.contains("require-when.every-login"))
            config.set("require-when.every-login", super.requireOnEveryLogin);
        else
            super.requireOnEveryLogin = config.getBoolean("require-when.every-login");

        if(!config.contains("storage-method"))
            config.set("storage-method", super.storageMethod.toString());
        else
            super.storageMethod = StorageMethod.valueOf(config.getString("storage-method", "H2").toUpperCase());

        if(!config.contains("storage-data.host"))
            config.set("storage-data.host", super.host);
        else
            super.host = config.getString("storage-data.host");

        if(!config.contains("storage-data.port"))
            config.set("storage-data.port", super.port);
        else
            super.port = config.getInt("storage-data.port");

        if(!config.contains("storage-data.database"))
            config.set("storage-data.database", super.database);
        else
            super.database = config.getString("storage-data.database");

        if(!config.contains("storage-data.username"))
            config.set("storage-data.username", super.username);
        else
            super.username = config.getString("storage-data.username");

        if(!config.contains("storage-data.password"))
            config.set("storage-data.password", super.password);
        else
            super.password = config.getString("storage-data.password");

        if(!config.contains("storage-data.table-prefix"))
            config.set("storage-data.table-prefix", super.tablePrefix);
        else
            super.tablePrefix = config.getString("storage-data.table-prefix");

        if(!config.contains("storage-data.collection-prefix"))
            config.set("storage-data.collection-prefix", super.collectionPrefix);
        else
            super.collectionPrefix = config.getString("storage-data.collection-prefix");


        if(!config.contains("storage-data.pool-settings.maximum-pool-size"))
            config.set("storage-data.pool-settings.maximum-pool-size", super.maximumPoolSize);
        else
            super.maximumPoolSize = config.getInt("storage-data.pool-settings.maximum-pool-size");

        if(!config.contains("storage-data.pool-settings.minimum-idle"))
            config.set("storage-data.pool-settings.minimum-idle", super.minimumIdle);
        else
            super.minimumIdle = config.getInt("storage-data.pool-settings.minimum-idle");

        if(!config.contains("storage-data.pool-settings.maximum-lifetime"))
            config.set("storage-data.pool-settings.maximum-lifetime", super.maximumLifetime);
        else
            super.maximumLifetime = config.getInt("storage-data.pool-settings.maximum-lifetime");

        if(!config.contains("storage-data.pool-settings.keep-alive-time"))
            config.set("storage-data.pool-settings.keep-alive-time", super.keepAliveTime);
        else
            super.keepAliveTime = config.getInt("storage-data.pool-settings.keep-alive-time");

        if(!config.contains("storage-data.pool-settings.connection-timeout"))
            config.set("storage-data.pool-settings.connection-timeout", super.connectionTimeout);
        else
            super.connectionTimeout = config.getInt("storage-data.pool-settings.connection-timeout");


        if(!config.contains("storage-data.mongodb-uri"))
            config.set("storage-data.mongodb-uri", super.mongodbURI);
        else
            super.mongodbURI = config.getString("storage-data.mongodb-uri");


        main.saveConfig();
    }
}
