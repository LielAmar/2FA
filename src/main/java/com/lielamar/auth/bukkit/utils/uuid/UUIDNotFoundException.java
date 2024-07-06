package com.lielamar.auth.bukkit.utils.uuid;

import org.jetbrains.annotations.NotNull;

public class UUIDNotFoundException extends Exception {

    public UUIDNotFoundException(@NotNull String explanation) {
        super(explanation);
    }
}