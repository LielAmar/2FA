package com.lielamar.auth.bukkit.utils.cmd;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StandaloneCommand extends SuperCommand {

    public StandaloneCommand(@NotNull String command, @Nullable String permission) {
        super(command, permission);
    }
    public StandaloneCommand(@NotNull String name, @Nullable CheckPermissionCallback checkPermissionCallback) { super(name, checkPermissionCallback); }


    @Override
    public final void subCommandNotFoundEvent(@NotNull CommandSender cs) {}

    @Override
    public final Command[] getSubCommands() { return new Command[0]; }


    @Override
    public @NotNull String getUsage() { return ""; }

    @Override
    public @NotNull String getDescription() { return ""; }

    @Override
    public @NotNull String[] getAliases() { return new String[0]; }
}