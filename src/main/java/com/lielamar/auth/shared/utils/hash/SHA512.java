package com.lielamar.auth.shared.utils.hash;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA512 implements Hash {

    @Override
    public String hash(String string) {
        return DigestUtils.sha512Hex(string);
    }
}
