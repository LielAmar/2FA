package com.lielamar.auth.core.storage;

public class UserSession {
    private String key;
    private String ip;
    private long enableDate;
    private long lastAuthDate;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getEnableDate() {
        return enableDate;
    }

    public void setEnableDate(long enableDate) {
        this.enableDate = enableDate;
    }

    public long getLastAuthDate() {
        return lastAuthDate;
    }

    public void setLastAuthDate(long lastAuthDate) {
        this.lastAuthDate = lastAuthDate;
    }
}
