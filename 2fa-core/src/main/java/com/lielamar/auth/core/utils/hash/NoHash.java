package com.lielamar.auth.core.utils.hash;

public class NoHash implements Hash {

    @Override
    public String hash(String string) {
        return string;
    }
}
