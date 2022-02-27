package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.utils.Version;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportCommand extends Command {

    private final TwoFactorAuthentication main;

    Date now = new Date();
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public ReportCommand(String name, TwoFactorAuthentication main) {
        super(name);

        this.main = main;
    }

    @Override
    public String[] getAliases() {
        return new String[] { "print", "info", "report" };
    }

    @Override
    public String[] getPermissions() {
        return new String[] { "2fa.report" };
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_PRINT_INFO_COMMAND.getMessage());
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] targets) {
        if(!hasPermissions(commandSender)) {
            main.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
        } else {
            commandSender.sendMessage(ChatColor.GREEN + "Creating a Bug Report file...");

            try {
                File dataFolder = main.getDataFolder();
                if(!dataFolder.exists()) dataFolder.mkdir();

                File reportFolder = new File(main.getDataFolder(), "reports");
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
                print.println("Loaded proxy: " + this.main.getAuthHandler().isProxyLoaded);
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
        }
    }
}