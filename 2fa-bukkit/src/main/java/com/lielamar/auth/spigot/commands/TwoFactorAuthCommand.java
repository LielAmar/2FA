package com.lielamar.auth.spigot.commands;

import com.lielamar.auth.spigot.commands.annotations.CommandData;
import com.lielamar.auth.spigot.utils.Version;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
@CommandData(name = "2fa", aliases = {})
public class TwoFactorAuthCommand implements CommandExecutor {
    private final Server server;
    private final Plugin plugin;

    @Inject
    public TwoFactorAuthCommand(final @NotNull Server server,
                                final @NotNull Plugin plugin) {
        this.server = server;
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Date now = new Date();
        sender.sendMessage(ChatColor.GREEN + "Creating a Bug Report file...");

        File reportFile = createReportFile(now);

        if (reportFile == null) {
            sender.sendMessage(ChatColor.RED + "Failed to create a Bug Report file!");
            return true;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile, true))) {
            writeReport(writer);
            sender.sendMessage(ChatColor.GREEN + "Created a Bug Report file. Use this for your Bug Report! :)");
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Failed to create a Bug Report file!");
        }

        return true;
    }

    private File createReportFile(Date now) {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                return null;
            }

            File reportFolder = new File(dataFolder, "reports");
            if (!reportFolder.exists() && !reportFolder.mkdirs()) {
                return null;
            }

            File file = new File(reportFolder, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(now) + "-PrintInfo.txt");
            if (!file.exists() && !file.createNewFile()) {
                return null;
            }

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeReport(PrintWriter writer) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        writer.println("Server Java and OS");
        writer.println("- Java Runtime Version: " + System.getProperty("java.runtime.version"));
        writer.println("- OS: " + System.getProperty("os.name"));
        writer.println();

        writer.println("Server Plugins");
        for (Plugin plugin : server.getPluginManager().getPlugins()) {
            writer.println("- " + plugin.getName() + " | " + plugin.getDescription().getVersion() + " | Enabled: " + plugin.isEnabled());
        }
        writer.println();

        writer.println("Server Jar, Versions and Information");
        writer.println("- Spigot Build version: " + server.getVersion());
        writer.println("- Server Version: " + server.getBukkitVersion());
        writer.println("- Version Instance: " + Version.getInstance().getServerVersion().getVersionName());
        writer.println("- Max Memory: " + (Runtime.getRuntime().maxMemory() / 1_000_000) + " MB");
        writer.println("- Free Memory: " + (Runtime.getRuntime().freeMemory() / 1_000_000) + " MB");
        writer.println("- Total Memory: " + (Runtime.getRuntime().totalMemory() / 1_000_000) + " MB");
        writer.println();

        writer.println("Communication Method & Proxies:");
        writer.println("- Using Bungeecord: " + Class.forName("org.spigotmc.SpigotConfig").getField("bungee").getBoolean(null));
//        writer.println("- Is Proxy Loaded: " + (plugin.getAuthHandler().getAuthCommunicationHandler() instanceof ProxyAuthCommunication));
//        writer.println("- Communication Method in config: " + plugin.getConfigHandler().getCommunicationMethod().name());
//        writer.println("- Communication Timeout in config: " + plugin.getConfigHandler().getCommunicationTimeout() + " ticks");
        writer.println();

        writer.println("Storage Information:");
//        writer.println("- Type of Storage: " + plugin.getConfigHandler().getStorageMethod().name());
//        writer.println("- Is external Storage loaded: " + plugin.getStorageHandler().isLoaded());
        writer.println();

        writer.println("Attach this file when creating an issue on GitHub: https://github.com/LielAmar/2FA/issues");
        writer.println("You can also join our discord server and talk to us there: https://discord.com/invite/NzgBrqR");
    }
}

