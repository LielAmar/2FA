package com.lielamar.auth.utils.hash;

import com.lielamar.auth.utils.Hash;

public class NoHash implements Hash {

    @Override
    public String hash(String string) {
        return string;
    }
}
