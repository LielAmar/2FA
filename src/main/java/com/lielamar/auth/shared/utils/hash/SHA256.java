package com.lielamar.auth.shared.utils.hash;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA256 implements Hash {

    public String hash(String string) {
        return DigestUtils.sha256Hex(string);
    }
}
