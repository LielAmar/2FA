package com.lielamar.auth.database;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface DatabaseModule {

    Map<UUID, String> cachedKeys = new HashMap<UUID, String>();

    String setSecretKey(UUID uuid, String secretKey);
    String getSecretKey(UUID uuid);
    boolean hasSecretKey(UUID uuid);
    void removeSecretKey(UUID uuid);

}