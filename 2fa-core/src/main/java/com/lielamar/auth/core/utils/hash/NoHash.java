package com.lielamar.auth.core.utils.hash;

import com.lielamar.auth.core.utils.Hash;

public class NoHash implements Hash {

    @Override
    public String hash(String string) {
        return string;
    }
}
