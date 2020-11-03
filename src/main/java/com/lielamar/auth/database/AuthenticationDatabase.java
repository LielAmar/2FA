package com.lielamar.auth.database;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface AuthenticationDatabase {

    Map<UUID, String> cachedKeys = new HashMap<>();

    boolean setupDatabase();

    String setSecretKey(UUID uuid, String secretKey);
    String getSecretKey(UUID uuid);
    boolean hasSecretKey(UUID uuid);
    void removeSecretKey(UUID uuid);
}
