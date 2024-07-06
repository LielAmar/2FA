package com.lielamar.auth.bukkit.utils.cmd;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Command {

    protected String command;
    protected @Nullable String permission;
    protected @Nullable CheckPermissionCallback checkPermissionCallback;

    public Command(@NotNull String command, @Nullable String permission) {
        this.command = command;
        this.permission = permission;
    }

    public Command(@NotNull String command, @Nullable CheckPermissionCallback checkPermissionCallback) {
        this.command = command;
        this.checkPermissionCallback = checkPermissionCallback;
    }


    public abstract void noPermissionEvent(@NotNull CommandSender cs);
    public abstract boolean runCommand(@NotNull CommandSender cs, @NotNull String[] args);
    public abstract List<String> tabOptions(@NotNull CommandSender cs, @NotNull String[] args);

    public abstract @NotNull String getUsage();
    public abstract @NotNull String getDescription();
    public abstract @NotNull String[] getAliases();


    public final @NotNull String getCommandName() { return command; }
    public final @Nullable String getPermission() { return permission; }


    public final boolean hasPermission(@NotNull CommandSender cs) {
        if(this.permission == null && this.checkPermissionCallback == null)
            return false;

        if(this.permission != null)
            return cs.hasPermission(this.permission);

        return this.checkPermissionCallback.hasPermission(cs);
    }

    public final void execute(CommandSender cs, @NotNull String[] args) {
        if(this.hasPermission(cs))
            this.runCommand(cs, args);
        else
            this.noPermissionEvent(cs);
    }
}