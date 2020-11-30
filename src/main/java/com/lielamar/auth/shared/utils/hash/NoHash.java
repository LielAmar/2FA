package com.lielamar.auth.shared.utils.hash;

public class NoHash implements Hash {

    public String hash(String string) {
        return string;
    }
}
