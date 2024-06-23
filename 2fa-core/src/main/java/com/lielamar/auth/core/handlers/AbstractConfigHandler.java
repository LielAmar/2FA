package com.lielamar.auth.core.handlers;


import com.lielamar.auth.core.storage.StorageMethod;
import dev.samstevens.totp.code.HashingAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractConfigHandler {
    protected Map<Class<?>, Boolean> disabledEvents = new HashMap<>();
    protected List<String> whitelistedCommands = new ArrayList<>();
    protected List<String> blacklistedCommands = new ArrayList<>();

    protected StorageMethod storageMethod = StorageMethod.JSON;
    protected String host = "localhost";
    protected int port = -1;
    protected String database = "auth";
    protected String username = "root";
    protected String password = "password";
    protected String tablePrefix = "2fa_";
    protected String collectionPrefix = "2fa_";

    protected HashingAlgorithm hashingAlgorithm = HashingAlgorithm.SHA256;

    protected int maximumPoolSize = 10;
    protected int minimumIdle = 10;
    protected int maximumLifetime = 1800000;
    protected int keepAliveTime = 0;
    protected int connectionTimeout = 5000;

    protected String mongodbURI = "";

    public Map<Class<?>, Boolean> getDisabledEvents() {
        return this.disabledEvents;
    }

    public List<String> getWhitelistedCommands() {
        return this.whitelistedCommands;
    }

    public List<String> getBlacklistedCommands() {
        return this.blacklistedCommands;
    }

    public StorageMethod getStorageMethod() {
        return this.storageMethod;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getTablePrefix() {
        return this.tablePrefix;
    }

    public String getCollectionPrefix() {
        return this.collectionPrefix;
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public int getMinimumIdle() {
        return this.minimumIdle;
    }

    public int getMaximumLifetime() {
        return this.maximumLifetime;
    }

    public int getKeepAliveTime() {
        return this.keepAliveTime;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public String getMongodbURI() {
        return this.mongodbURI;
    }

    public abstract void reload();
}
