package com.lielamar.auth.core.storage;

public class UserSession {
    private long enableDate;
    private String key;
    private String ip;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getEnableDate() {
        return enableDate;
    }

    public void setEnableDate(long enableDate) {
        this.enableDate = enableDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
