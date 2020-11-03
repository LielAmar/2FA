package com.lielamar.auth.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lielamar.auth.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

public class AuthCommand implements CommandExecutor {

    private final Main main;

    public AuthCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("2fa")) {
            if(!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "You must be a player in order to use 2 Factor Authentication!");
                return false;
            }

            Player player = (Player)cs;

            // If the amount of arguments is 0 we want to either set up 2fa/send the message to authenticate
            // If the amount of arguments is greater than 0, we want to authenticate player/remove their key
            if(args.length == 0) {
                return this.main.getAuthDatabaseManager().setup2FA(player);
            } else {
                String codeRaw = Arrays.toString(args);

                try {
                    int code = Integer.parseInt(codeRaw);
                    return this.main.getAuthDatabaseManager().authenticatePlayer(player, code);
                } catch(IllegalArgumentException e) {
                    if(codeRaw.equalsIgnoreCase("remove") || codeRaw.equalsIgnoreCase("reset")) {
                        if(args.length == 1) {
                            return this.main.getAuthDatabaseManager().removePlayerSecretKey(player, player.getUniqueId());
                        } else {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    UUID targetUUID = fetchUUID(args[1]);
                                    main.getAuthDatabaseManager().removePlayerSecretKey(player, targetUUID);
                                }
                            }.runTaskAsynchronously(this.main);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Communicates with Mojang's API to get a player's uuid through their IGN
     *
     * @param name   Name to get the UUID of
     * @return       UUID of the player
     */
    public UUID fetchUUID(String name) {
        try {
            String MOJANG_API = "https://api.mojang.com/users/profiles/minecraft/%s";
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(MOJANG_API, name)).openConnection();
            connection.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JsonElement json = new JsonParser().parse(response.toString());
            String uuid = json.getAsJsonObject().get("id").getAsString();

            return UUID.fromString(uuid);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}