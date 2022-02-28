package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.utils.Version;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportCommand extends StandaloneCommand {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private final TwoFactorAuthentication plugin;

    private Date now;

    public ReportCommand(TwoFactorAuthentication plugin) {
        super(Constants.reportCommand.getA(), Constants.reportCommand.getB());

        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        now = new Date();

        commandSender.sendMessage(ChatColor.GREEN + "Creating a Bug Report file...");

        try {
            File dataFolder = this.plugin.getDataFolder();
            if(!dataFolder.exists()) dataFolder.mkdir();

            File reportFolder = new File(this.plugin.getDataFolder(), "reports");
            if(!reportFolder.exists()) reportFolder.mkdir();

            File file = new File(reportFolder, format.format(now).replaceAll(":", "-") + "-PrintInfo.txt");
            if(!file.exists()) file.createNewFile();

            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter print = new PrintWriter(fileWriter);

            print.println("Server Java and OS:");
            print.println("");
            print.println(System.getProperty("java.runtime.version") + "" + (System.getProperty("os.name")));
            print.println("");
            print.println("Server Plugins:");
            print.println("");

            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
            for(Plugin p : plugins)
                print.println(p.getName() + " - " + p.getDescription().getVersion() + " + " + p.getDescription().getAuthors() + " | Enabled: " + p.isEnabled());

            print.println("");
            print.println("Server Jar & Version:");
            print.println(Bukkit.getVersion());
            print.println(Bukkit.getBukkitVersion());
            print.println("ServerVersion: " + Version.getInstance().getServerVersion().getVersionName());
            print.println("NMSVersion: " + Version.getInstance().getNMSVersion().getVersionName());
            print.println("");
            print.println("Using bungeecord: " + Class.forName("org.spigotmc.SpigotConfig").getField("bungee").getBoolean(null));
            print.println("Loaded proxy: " + false /* this.main.getAuthHandler().isProxyLoaded */);
            print.println("");
            print.println("Send this on Github Issue Tracker: https://github.com/LielAmar/2FA/issues");
            print.println("Or create a Ticket in the discord server: https://discord.com/invite/NzgBrqR");
            print.flush();
            print.close();

            commandSender.sendMessage(ChatColor.GREEN + "Created a Bug Report file, Use this for your Bug Report! :)");
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();

            commandSender.sendMessage(ChatColor.RED + "Failed to create a Bug Report file!");
        }
        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        return new ArrayList<>();
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
    }

    @Override
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_PRINT_INFO_COMMAND.getMessage());
    }

    @Override
    public String[] getAliases() {
        return new String[] { "print", "info", "report" };
    }
}