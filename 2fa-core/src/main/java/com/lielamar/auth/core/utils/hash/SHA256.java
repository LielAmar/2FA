package com.lielamar.auth.core.utils.hash;

import com.lielamar.auth.core.utils.Hash;
import org.apache.commons.codec.digest.DigestUtils;

public class SHA256 implements Hash {

    @Override
    public String hash(String string) {
        return DigestUtils.sha256Hex(string);
    }
}
