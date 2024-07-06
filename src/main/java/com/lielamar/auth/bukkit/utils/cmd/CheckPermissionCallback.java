package com.lielamar.auth.bukkit.utils.cmd;

import org.bukkit.command.CommandSender;

public interface CheckPermissionCallback {

    boolean hasPermission(CommandSender commandSender);
}