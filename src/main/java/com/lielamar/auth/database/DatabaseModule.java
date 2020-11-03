package com.lielamar.auth.database;

import java.util.UUID;

public interface DatabaseModule {

    String setSecretKey(UUID uuid, String secretKey);
    String getSecretKey(UUID uuid);
    void removeSecretKey(UUID uuid);

}