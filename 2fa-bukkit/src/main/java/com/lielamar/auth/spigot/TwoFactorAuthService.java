package com.lielamar.auth.spigot;

import com.lielamar.auth.api.ITwoFactorAuthService;
import com.lielamar.auth.api.ITwoFactorAuthPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Singleton
public final class TwoFactorAuthService implements ITwoFactorAuthService {
    @NotNull private final ITwoFactorAuthPlugin plugin;

    @Inject
    @Contract(pure = true)
    public TwoFactorAuthService(final @NotNull ITwoFactorAuthPlugin plugin) {
        this.plugin = plugin;
    }
}