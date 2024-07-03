package com.lielamar.auth.core.utils;

public class AuthTracker {

    private int authentications;

    public AuthTracker() {
        this.authentications = 0;
    }

    public int getAuths() {
        return authentications;
    }

    public void setAuths(int authentications) {
        this.authentications = authentications;
    }

    public void increment() {
        this.authentications++;
    }
}
