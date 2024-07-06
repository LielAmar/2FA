package com.lielamar.auth.bukkit.utils.cmd;

import com.lielamar.auth.bukkit.utils.arrays.ArraysUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ParentCommand extends CommandWithSubCommands {

    public ParentCommand(@NotNull String name, @Nullable String permission) { super(name, permission); }
    public ParentCommand(@NotNull String name, @Nullable CheckPermissionCallback checkPermissionCallback) { super(name, checkPermissionCallback); }


    public abstract boolean runMasterCommand(@NotNull CommandSender commandSender, @NotNull String[] args);
    public abstract List<String> maserTabOptions(@NotNull CommandSender commandSender, @NotNull String[] args);


    @Override
    public final boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        // Tries to run the subcommand first, then trying to run the master command if no sub command was ran.

        if(super.hasPermission(commandSender)) {
            if(args.length != 0) {
                Command subCommand = this.getSubCommand(args[0]);

                if(subCommand != null) {
                    if(subCommand.hasPermission(commandSender))
                        return subCommand.runCommand(commandSender, (getSubCommands().length > 0 ? ArraysUtils.removeFirstElement(args.clone()) : args.clone()));
                    else {
                        subCommand.noPermissionEvent(commandSender);
                        return false;
                    }
                }
            }

            return runMasterCommand(commandSender, args.clone());
        }

        noPermissionEvent(commandSender);
        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if(super.hasPermission(commandSender)) {
            if(args.length == 0)
                return this.maserTabOptions(commandSender, args.clone());

            Command subCommand = getSubCommand(args[0]);

            if(subCommand != null)
                return subCommand.tabOptions(commandSender, (getSubCommands().length > 0 ? ArraysUtils.removeFirstElement(args.clone()) : args.clone()));

            List<String> opt = this.maserTabOptions(commandSender, args.clone());
            options.addAll(opt == null ? new ArrayList<>() : opt);

            for(Command subCmd : getSubCommands()) {
                if(subCmd.getPermission() == null || commandSender.hasPermission(subCmd.getPermission())) {
                    if(subCmd.getCommandName().toLowerCase().startsWith(args[0].toLowerCase()))
                        options.add(subCmd.getCommandName());

                    Arrays.stream(subCmd.getAliases())
                            .filter(alias -> alias.toLowerCase().startsWith(args[0].toLowerCase()))
                            .forEach(options::add);
                }
            }
        }

        return options;
    }
}