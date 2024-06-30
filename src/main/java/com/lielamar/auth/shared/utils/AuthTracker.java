package com.lielamar.auth.shared.utils;

public class AuthTracker {

    private int authentications;

    public AuthTracker() {
        this.authentications = 0;
    }

    public int getAuthentications() {
        return authentications;
    }

    public void setAuthentications(int authentications) {
        this.authentications = authentications;
    }

    public int incrementAuths() {
        return ++authentications;
    }
}
