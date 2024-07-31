package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.communication.ProxyAuthCommunication;
import com.lielamar.auth.bukkit.utils.cmd.StandaloneCommand;
import com.lielamar.auth.bukkit.utils.cmd.SuperCommand;
import com.lielamar.auth.bukkit.utils.version.Version;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportCommand extends StandaloneCommand {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private final TwoFactorAuthentication plugin;
    private final SuperCommand parent;

    public ReportCommand(@NotNull TwoFactorAuthentication plugin, @NotNull SuperCommand parent) {
        super(Constants.reportCommand.getA(), Constants.reportCommand.getB());

        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        Date now = new Date();

        commandSender.sendMessage(ChatColor.GREEN + "Creating a Bug Report file...");
        PrintWriter writer;
        try {
            File dataFolder = this.plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File reportFolder = new File(this.plugin.getDataFolder(), "reports");
            if (!reportFolder.exists()) {
                reportFolder.mkdir();
            }

            File file = new File(reportFolder, format.format(now).replaceAll(":", "-") + "-PrintInfo.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, true);
            writer = new PrintWriter(fileWriter);

            writer.println("Server Java and OS");
            writer.println("- Java Runtime Version: " + System.getProperty("java.runtime.version"));
            writer.println("- OS: " + System.getProperty("os.name"));

            writer.println("");

            writer.println("Server Plugins");

            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                writer.println("- " + plugin.getName() + " | " + plugin.getDescription().getVersion() + " | Enabled: " + plugin.isEnabled());
            }

            writer.println("");

            writer.println("Server Jar, Versions and Information");
            writer.println("- Spigot Build version: " + Bukkit.getVersion());
            writer.println("- Server Version: " + Bukkit.getBukkitVersion());
            writer.println("- Version Instance: " + Bukkit.getServer().getBukkitVersion().split("-")[0].replaceAll("\\.", "_"));
            writer.println("- Max Memory: " + (Runtime.getRuntime().maxMemory() / 1000000) + " MB");
            writer.println("- Free Memory: " + (Runtime.getRuntime().freeMemory() / 1000000) + " MB");
            writer.println("- Total Memory: " + (Runtime.getRuntime().totalMemory() / 1000000) + " MB");

            writer.println("");

            writer.println("Communication Method & Proxies: ");
            writer.println("- Proxy Software: " + getProxy());
            writer.println("- Is Proxy Loaded: " + (this.plugin.getAuthHandler().getAuthCommunicationHandler() instanceof ProxyAuthCommunication));
            writer.println("- Communication Method in config: " + this.plugin.getConfigHandler().getCommunicationMethod().name());
            writer.println("- Communication Timeout in config: " + this.plugin.getConfigHandler().getCommunicationTimeout() + " ticks");

            writer.println("");

            writer.println("Storage Information: ");
            writer.println("- Type of Storage: " + this.plugin.getConfigHandler().getStorageMethod().name());
            writer.println("- Is external Storage loaded: " + this.plugin.getStorageHandler().isLoaded());

            writer.println("");

            writer.println("Attach this file when creating an issue on GitHub: https://github.com/LielAmar/2FA/issues");
            writer.println("You can also join our discord server and talk to us there: https://discord.com/invite/NzgBrqR");

            writer.flush();
            writer.close();

            commandSender.sendMessage(ChatColor.GREEN + "Created a Bug Report file, Use this for your Bug Report! :)");
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + "Failed to create a Bug Report file!");
        }
        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.parent.noPermissionEvent(commandSender);
    }

    @Override
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_PRINT_INFO_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"print", "info", "report"};
    }

    private String getProxy() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        if (isUsingBungee()) {
            return "BUNGEE";
        } else if (isUsingVelocity()) {
            return "VELOCITY";
        } else {
            return "NONE";
        }
    }

    private boolean isUsingBungee() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return Class.forName("org.spigotmc.SpigotConfig").getField("bungee").getBoolean(null);
    }

    // This should detect if using velocity above and from paper version 1.13.1 build 377
    // commit https://github.com/PaperMC/paper/commit/7141632abfd3ede89a1e3749c4e40ef6d0be8574
    private boolean isUsingVelocity() {
        if (Version.getInstance().getServerVersion().above(Version.ServerVersion.v1_19)) {
            try {
                Class<?> globalConfigClass = Class.forName("io.papermc.paper.configuration.GlobalConfiguration"); // Replace with the actual package name
                Object globalConfigInstance = globalConfigClass.getMethod("get").invoke(null);

                Field proxiesField = globalConfigClass.getDeclaredField("proxies");
                proxiesField.setAccessible(true);
                Object proxiesInstance = proxiesField.get(globalConfigInstance);

                Field velocityField = proxiesInstance.getClass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Object velocityInstance = velocityField.get(proxiesInstance);

                Field enabledField = velocityInstance.getClass().getDeclaredField("enabled");
                enabledField.setAccessible(true);
                return enabledField.getBoolean(velocityInstance);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                Class<?> paperConfigClass = Class.forName("com.destroystokyo.paper.PaperConfig");
                Field velocitySupportField = paperConfigClass.getDeclaredField("velocitySupport");
                velocitySupportField.setAccessible(true);

                return velocitySupportField.getBoolean(null);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
