package com.lielamar.auth.bukkit.utils.uuid;

import org.jetbrains.annotations.NotNull;

public class InvalidResponseException extends Exception {

    public InvalidResponseException(@NotNull String explanation) {
        super(explanation);
    }
}