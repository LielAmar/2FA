package com.lielamar.auth.bukkit.utils.uuid;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UUIDCallback {

    void run(@Nullable UUID uuid);

}