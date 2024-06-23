package com.lielamar.auth.spigot.commands;

import com.lielamar.auth.spigot.commands.annotations.CommandData;
import jakarta.inject.Inject;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PluginCommandsRegisterer implements ICommandsRegisterer {
    private final JavaPlugin effectsPlugin;
    private final List<CommandExecutor> commands;

    @Inject
    public PluginCommandsRegisterer(final @NotNull JavaPlugin plugin,
                                    final @NotNull List<CommandExecutor> commands) {
        this.effectsPlugin = plugin;
        this.commands = commands;
    }

    @Override
    public void registerCommands() {
        commands.remove(effectsPlugin); // Remove the main class, since Plugin implements TabExecutor

        PluginCommand pluginCommand;
        for (CommandExecutor command : commands) {
            CommandData data = command.getClass().getAnnotation(CommandData.class);
            pluginCommand = effectsPlugin.getCommand(data.name());
            if (pluginCommand != null) {
                pluginCommand.setExecutor(command);
                pluginCommand.setAliases(List.of(data.aliases()));
                if (command instanceof TabCompleter tabCompleter) pluginCommand.setTabCompleter(tabCompleter);
            }
        }
    }
}
