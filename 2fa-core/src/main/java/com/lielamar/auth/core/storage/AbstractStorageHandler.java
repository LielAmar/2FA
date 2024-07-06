package com.lielamar.auth.core.storage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lielamar.auth.core.config.AbstractConfigHandler;
import com.lielamar.auth.core.storage.json.JSONStorage;
import com.lielamar.auth.core.storage.mongodb.MongoDBStorage;
import com.lielamar.auth.core.storage.sql.SQLStorage;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Factory
public abstract class AbstractStorageHandler {
    protected Cache<UUID, UserSession> cache;
    public static boolean isLoaded;

    public AbstractStorageHandler() {

        // todo: allow user to configure the cache's properties as well as query the cache's data (update, remove etc...)
        cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(75)
                .build();
    }

    public abstract String setKey(UUID uuid, String secretKey);

    public abstract String getKey(UUID uuid);

    public abstract boolean hasKey(UUID uuid);

    public abstract void removeKey(UUID uuid);


    public abstract String setIP(UUID uuid, String lastIP);

    /**
     * Returns the IP of the player whose UUID is uuid
     *
     * @param uuid UUID of the player to get the IP of
     * @return Last IP of the player
     */
    public abstract String getIP(UUID uuid);

    /**
     * Checks whether a player whose UUID is uuid has a Last IP
     *
     * @param uuid UUID of the player to check the IP of
     * @return Whether the player has a Last IP
     */
    public abstract boolean hasIP(UUID uuid);

    /**
     * Sets the Enable Date of the player whose UUID is uuid
     *
     * @param uuid UUID of the player to set the Enable Date of
     * @param enableDate Enable Date to set
     * @return The set Enable Date
     */
    public abstract long setEnableDate(UUID uuid, long enableDate);

    /**
     * Returns the Enable Date of the player whose UUID is uuid
     *
     * @param uuid UUID of the player to get the Enable Date of
     * @return Enable Date of the player
     */
    public abstract long getEnableDate(UUID uuid);

    /**
     * Checks whether a player whose UUID is uuid has an Enable Date
     *
     * @param uuid UUID of the player to check the Enable Date of
     * @return Whether the player has an Enable Date
     */
    public abstract boolean hasEnableDate(UUID uuid);

    /**
     * Unloads everything related to the storage type
     */
    public abstract void unload();
    
    /**
     * Check if external storage was loaded
     */
    public abstract boolean isLoaded();

    @Bean
    @Singleton
    public AbstractStorageHandler loadStorageHandler(final @NotNull AbstractConfigHandler configHandler,
                                                     final @Value("datafolder") String absolutePath) {
        try {
            isLoaded = true;
            return switch (configHandler.getStorageMethod()) {
                case MYSQL -> new SQLStorage("com.mysql.cj.jdbc.MysqlDataSource",
                        configHandler.getHost(), configHandler.getDatabase(), configHandler.getUsername(), configHandler.getPassword(), configHandler.getPort(),
                        configHandler.getTablePrefix(), configHandler.getMaximumPoolSize(), configHandler.getMinimumIdle(), configHandler.getMaximumLifetime(), configHandler.getKeepAliveTime(), configHandler.getConnectionTimeout());
                case H2 -> new SQLStorage("org.h2.jdbcx.JdbcDataSource",
                        configHandler.getHost(), configHandler.getDatabase(), configHandler.getUsername(), configHandler.getPassword(), configHandler.getPort(),
                        configHandler.getTablePrefix(), configHandler.getMaximumPoolSize(), configHandler.getMinimumIdle(), configHandler.getMaximumLifetime(), configHandler.getKeepAliveTime(), configHandler.getConnectionTimeout());
                case MARIADB -> new SQLStorage("org.mariadb.jdbc.MariaDbDataSource",
                        configHandler.getHost(), configHandler.getDatabase(), configHandler.getUsername(), configHandler.getPassword(), configHandler.getPort(),
                        configHandler.getTablePrefix(), configHandler.getMaximumPoolSize(), configHandler.getMinimumIdle(), configHandler.getMaximumLifetime(), configHandler.getKeepAliveTime(), configHandler.getConnectionTimeout());
                case POSTGRESQL -> new SQLStorage("org.postgresql.ds.PGSimpleDataSource",
                        configHandler.getHost(), configHandler.getDatabase(), configHandler.getUsername(), configHandler.getPassword(), configHandler.getPort(),
                        configHandler.getTablePrefix(), configHandler.getMaximumPoolSize(), configHandler.getMinimumIdle(), configHandler.getMaximumLifetime(), configHandler.getKeepAliveTime(), configHandler.getConnectionTimeout());
                case MONGODB ->
                        new MongoDBStorage(configHandler.getHost(), configHandler.getDatabase(), configHandler.getUsername(), configHandler.getPassword(), configHandler.getPort(),
                                configHandler.getCollectionPrefix(), configHandler.getMongodbURI());
                default -> // JSON
                        new JSONStorage(absolutePath);
            };
        } catch (Exception exception) {
            isLoaded = false;
            exception.printStackTrace();
            System.out.println("Couldn't load the Database you specified for the above reason. Defaulting to JSON!");
            return new JSONStorage(absolutePath);
        }
    }
}
