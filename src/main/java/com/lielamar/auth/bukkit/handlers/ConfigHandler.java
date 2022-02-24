package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.shared.storage.StorageMethod;
import com.lielamar.lielsutils.files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

public class ConfigHandler extends com.lielamar.auth.shared.handlers.ConfigHandler {

    private final FileManager.Config config;

    public ConfigHandler(FileManager fileManager) {
        this.config = fileManager.getConfig(super.configFileName);

        this.reload();
    }

    @Override
    public void reload() {
        config.setHeader(Arrays.asList(
                "# Thank you for using the Two Factor Authentication plugin by Liel Amar.",
                "# Special thanks to Connor Linfoot for developing the original MC2FA plugin.",
                "# For support, you can contact me through my discord server: https://discord.gg/NzgBrqR.",
                "",
                "# If you have any problems connecting to the database, whether it be MySQL or Mongo,",
                "# please delete this file, start up your server once again and re-set the values.",
                "",
                "# Github: https://github.com/LielAmar/2FA"
        ));

        if(!config.contains("qr-code-service")) {
            config.set("qr-code-service", super.qrCodeURL);
            config.addComment("qr-code-service", "# Service to use when generating QR codes");
        } else
            super.qrCodeURL = config.getString("qr-code-service");

        if(!config.contains("check-for-updates")) {
            config.set("check-for-updates", super.checkForUpdates);
            config.addComment("check-for-updates", "# Whether to notify when there's an update available");
        } else
            super.checkForUpdates = config.getBoolean("check-for-updates");

        if(!config.contains("server-name")) {
            config.set("server-name", super.serverName);
            config.addComment("server-name", "# Your server name. This will be the name of the service displayed in the authenticator app");
        } else
            super.serverName = config.getString("server-name");


        if(!config.contains("advise-2fa")) {
            config.set("advise-2fa", super.advise2FA);
            config.addComment("advise-2fa", "# Whether to send a message advising to use 2FA if the player has permissions to use it");
        } else
            super.advise2FA = config.getBoolean("advise-2fa");

        if(!config.contains("reserved-maps")) {
            config.set("reserved-maps", super.reservedMaps);
            config.addComment("reserved-maps", "# Amount of maps the plugin will reserve for it to display QR Codes");
        } else
            super.reservedMaps = config.getInt("reserved-maps");

        if(!config.contains("map-ids")) {
            config.set("map-ids", new int[0]);
            config.addComment("map-ids", "# DO NOT TOUCH THIS. This option saves the map IDs used by the plugin. Changing this may cause problems with maps!");
        } else
            super.mapIDs = new int[super.reservedMaps];

        int index = 0;
        for(int i : config.getIntegerList("map-ids"))
            super.mapIDs[index++] = i;

        if(!config.contains("ip-hash")) {
            config.set("ip-hash", super.ipHashType);
            config.addComments("ip-hash", new String[] {
                    "# The hash method to use for players' IP addresses",
                    "# - SHA256",
                    "# - SHA512",
                    "# - NONE (no hash - not recommended!)"
            });
        } else
            super.ipHashType = config.getString("ip-hash");

        if(!config.contains("player-reload-delay")) {
            config.set("player-reload-delay", super.reloadDelay);
            config.addComments("player-reload-delay", new String[] {
                    "# How much delay should the plugin apply to loading players when the server reloads",
                    "# This is useful when you have multiple databases with different latencies",
                    "# You can use it to ensure your permissions plugin loads before the players are reloaded",
                    "# * It's best to set the value to 0 if you use the same storage type AND storage (server/file), because you won't have latency differences anyways"
            });
        } else
            super.reloadDelay = config.getInt("player-reload-delay");

        ConfigurationSection disabledEventsSection = config.getConfigurationSection("disabled-events");
        try {
            if(disabledEventsSection != null) {
                for(String key : disabledEventsSection.getKeys(false))
                    super.disabledEvents.put(ShorterEvents.valueOf(key.toUpperCase().replaceAll("-", "_")).getMatchingEvent(), disabledEventsSection.getBoolean(key));
            } else {
                config.createSection("disabled-events");
                config.addComments("disabled-events", new String[] {
                        "# Events to disable if a player is not authenticated yet",
                        "# true - will block the event",
                        "# false - will allow the event"
                });
            }
        } catch(IllegalArgumentException exception) {
            exception.printStackTrace();
            Bukkit.getServer().getLogger().severe("The plugin detected that your configuration is having some incompatible Events in the \"disabled-events\" section." +
                    "Please re-check your configuration and make sure the Disabled Events names are correct!");
        }

        if(!config.contains("whitelisted-commands")) {
            config.set("whitelisted-commands", super.whitelistedCommands);
            config.addComments("whitelisted-commands", new String[] {
                    "# If the \"commands\" event is disabled, you can whitelist specific commands with the below setting",
                    "# These commands only will executed successfully. Anything else will be blocked."
            });
        } else
            super.whitelistedCommands = config.getStringList("whitelisted-commands");

        if(!config.contains("blacklisted-commands")) {
            config.set("blacklisted-commands", super.blacklistedCommands);
            config.addComments("blacklisted-commands", new String[] {
                    "# If the \"commands\" event is not disabled, you can blacklist specific commands with the below setting",
                    "# These commands only will be blacklisted. Anything else will be permitted"
            });
        } else
            super.blacklistedCommands = config.getStringList("blacklisted-commands");

        if(!config.contains("require-when")) {
            config.addComments("require-when", new String[] {
                    "# When should the plugin require players to authenticate using their 2FA?",
                    "# If your server uses a proxy, please include this plugin in your proxy /plugins/ folder",
                    "# This way, authentications will be handled on the proxy level and will the state will be global across all servers"
            });
        }

        if(!config.contains("require-when.ip-changes")) {
            config.set("require-when.ip-changes", super.requireOnIPChange);
            config.addComment("require-when.ip-changes", "  # When the player's IP address changes");
        } else
            super.requireOnIPChange = config.getBoolean("require-when.ip-changes");

        if(!config.contains("require-when.every-login")) {
            config.set("require-when.every-login", super.requireOnEveryLogin);
            config.addComment("require-when.every-login", "  # On every login");
        } else
            super.requireOnEveryLogin = config.getBoolean("require-when.every-login");

        if(!config.contains("tp-before-auth")) {
            config.set("tp-before-auth.enable", super.tpBeforeAuth);
            config.set("tp-before-auth.location.x", 0);
            config.set("tp-before-auth.location.y", 100);
            config.set("tp-before-auth.location.z", 0);
            config.set("tp-before-auth.location.yaw", 0);
            config.set("tp-before-auth.location.pitch", 0);
            config.set("tp-before-auth.location.world", "world");
            config.addComment("tp-before-auth", "# Should the plugin teleport players that need to authenticate to a designated location?");
        } else {
            super.tpBeforeAuth = config.getBoolean("tp-before-auth.enable");
            World world = Bukkit.getWorld(config.getString("tp-before-auth.location.world", "world"));

            if(world != null) {
                super.tpBeforeAuthLocation = new Location(
                        world,
                        config.getDouble("tp-before-auth.location.x"),
                        config.getDouble("tp-before-auth.location.y"),
                        config.getDouble("tp-before-auth.location.z"),
                        (float) config.getDouble("tp-before-auth.location.yaw"),
                        (float) config.getDouble("tp-before-auth.location.pitch"));
            }
        }

        if(!config.contains("tp-after-auth")) {
            config.set("tp-after-auth.enable", super.tpAfterAuth);
            config.set("tp-after-auth.location.x", 0);
            config.set("tp-after-auth.location.y", 100);
            config.set("tp-after-auth.location.z", 0);
            config.set("tp-after-auth.location.yaw", 0);
            config.set("tp-after-auth.location.pitch", 0);
            config.set("tp-after-auth.location.world", "world");
            config.addComment("tp-after-auth", "# Should the plugin teleport players to a designated location right after they authenticated?");
        } else {
            super.tpAfterAuth = config.getBoolean("tp-after-auth.enable");
            World world = Bukkit.getWorld(config.getString("tp-after-auth.location.world", "world"));

            if(world != null) {
                super.tpAfterAuthLocation = new Location(
                        world,
                        config.getDouble("tp-after-auth.location.x"),
                        config.getDouble("tp-after-auth.location.y"),
                        config.getDouble("tp-after-auth.location.z"),
                        (float) config.getDouble("tp-after-auth.location.yaw"),
                        (float) config.getDouble("tp-after-auth.location.pitch"));
            }
        }

        if(!config.contains("storage-method")) {
            config.set("storage-method", super.storageMethod.toString());
            config.addComments("storage-method", new String[] {
                    "# Possible methods for the plugin to store data",
                    "#",
                    "# Local",
                    "# - JSON",
                    "#",
                    "# Remote",
                    "# - MYSQL",
                    "# - MARIADB",
                    "# - POSTGRESQL",
                    "# - MONGODB",
                    "#",
                    "# If your server uses a proxy, it is recommended to use a Remote Storage Type!"
            });
        } else
            super.storageMethod = StorageMethod.valueOf(config.getString("storage-method", "H2").toUpperCase());

        if(!config.contains("storage-data.host")) {
            config.set("storage-data.host", super.host);
            config.addComment("storage-data.host", "  # The database host");
        } else
            super.host = config.getString("storage-data.host");

        if(!config.contains("storage-data.port")) {
            config.set("storage-data.port", super.port);
            config.addComment("storage-data.port", "  # by default, -1 will use the default port. If you need to specific a different port, changes this");
        } else
            super.port = config.getInt("storage-data.port");

        if(!config.contains("storage-data.database")) {
            config.set("storage-data.database", super.database);
            config.addComment("storage-data.database", "  # The name of the database to use when storing data");
        } else
            super.database = config.getString("storage-data.database");

        if(!config.contains("storage-data.username")) {
            config.set("storage-data.username", super.username);
            config.addComments("storage-data.username", new String[] {
                    "  # The credentials of the database",
                    "  # Leave blank for no authentication"
            });
        } else
            super.username = config.getString("storage-data.username");

        if(!config.contains("storage-data.password"))
            config.set("storage-data.password", super.password);
        else
            super.password = config.getString("storage-data.password");

        if(!config.contains("storage-data.table-prefix")) {
            config.set("storage-data.table-prefix", super.tablePrefix);
            config.addComment("storage-data.table-prefix", "  # The table & collection prefix for SQL & MongoDB respectively");
        } else
            super.tablePrefix = config.getString("storage-data.table-prefix");

        if(!config.contains("storage-data.collection-prefix"))
            config.set("storage-data.collection-prefix", super.collectionPrefix);
        else
            super.collectionPrefix = config.getString("storage-data.collection-prefix");


        if(!config.contains("storage-data.pool-settings")) {
            config.addComment("storage-data.pool-settings", "  # Settings for SQL connection pool");
        }

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


        if(!config.contains("storage-data.mongodb-uri")) {
            config.set("storage-data.mongodb-uri", super.mongodbURI);
            config.addComments("storage-data.mongodb-uri", new String[] {
                    "  # If you want to use a URI to connect to MongoDB, set the uri here. This will cause the plugin to ignore every other setting",
                    "  # Leave it empty if you don't want to use a URI."
            });
        } else
            super.mongodbURI = config.getString("storage-data.mongodb-uri");


        config.saveConfig();
    }
}
