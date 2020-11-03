package com.lielamar.auth.database;

import com.lielamar.auth.Main;
import com.lielamar.auth.database.json.AuthJSON;
import com.lielamar.auth.database.mongodb.AuthMongoDB;
import com.lielamar.auth.database.mysql.AuthMySQL;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AuthenticationDBManager {

    private final Main main;

    private AuthenticationDatabase database;

    public AuthenticationDBManager(Main main) {
        this.main = main;

        this.loadDatabase().setup();
    }

    public AuthenticationDatabase getDatabase() {
        return this.database;
    }


    /**
     * Loads the correct database (MySQL, MongoDB or JSON)
     *
     * @return   The database loaded
     */
    private AuthenticationDatabase loadDatabase() {
        if(main.getConfig().contains("MySQL.enabled") && main.getConfig().getBoolean("MySQL.enabled"))
            this.database = new AuthMySQL(this.main);
        else if(main.getConfig().contains("MongoDB.enabled") && main.getConfig().getBoolean("MongoDB.enabled"))
            this.database = new AuthMongoDB(this.main);
        else
            this.database = new AuthJSON(this.main);

        return this.database;
    }

    /**
     * Reloads the database
     *
     * @return   New database
     */
    public AuthenticationDatabase reload() {
        this.loadDatabase().setup();
        return this.database;
    }


    /**
     * Handles the set up of a 2FA of a player
     *
     * @param player   player to handle the setup of a 2FA of
     * @return         Whether or not setup was successful
     */
    public boolean setup2FA(Player player) {
        // If the player is locked we want to demand the code
        if(this.main.getAuthManager().isLocked(player)) {
            this.demandCode(player);
            return true;
        }

        // If the player doesn't have permissions we want to skip
        if(!player.hasPermission("2fa.setup")) {
            player.sendMessage(ChatColor.RED + "You don't have permissions to set up 2FA!");
            return false;
        }

        // Generate a key for the player
        this.generateKey(player);
        return true;
    }

    /**
     * Handles the authentication of a player
     *
     * @param player   Player to authenticate
     * @param code     Code provided by the player
     * @return         Whether or not authenticating was successful
     */
    public boolean authenticatePlayer(Player player, int code) {
        // If the player doesn't have 2FA
        if(!this.main.getAuthManager().hasAuthentication(player)) {
            player.sendMessage(ChatColor.RED + "You don't have 2FA set up! Use /2fa to set it up first!");
            return false;
        }

        // If the player has 2FA but has no permission to use it
        if(!player.hasPermission("2fa.use")) {
            this.main.getAuthManager().unlockPlayer(player);
            player.sendMessage(ChatColor.RED + "You don't have permissions to use /2FA so you were authenticated automatically");
            return true;
        }

        // If the player is locked we want to try and verify them
        if(this.main.getAuthManager().isLocked(player)) {
            if(!this.main.getAuthManager().authenticate(player, code)) {
                player.sendMessage(ChatColor.RED + "Wrong/Expired code!");
                return false;
            }

            player.sendMessage(ChatColor.YELLOW + "Authenticated! Welcome back :)");
            return true;
        }
        return false;
    }

    /**
     * Remove the secret key of a player
     *
     * @param commandSender    Player who committed the action
     * @param secretKeyOwner   Player to reset the secret key of
     * @return                 Whether or not removing the secret key was successful
     */
    public boolean removePlayerSecretKey(CommandSender commandSender, UUID secretKeyOwner) {
        if(commandSender instanceof Player && ((Player)commandSender).getUniqueId() == secretKeyOwner) {
            if(!commandSender.hasPermission("2fa.remove")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have permissions to remove your own 2FA!");
                return false;
            }
        } else {
            if(!commandSender.hasPermission("2fa.admin")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have permissions to remove other's 2FA!");
                return false;
            }
        }
        this.removeKey(secretKeyOwner);
        return true;
    }

    /**
     * Resets a player's secret key
     *
     * @param uuid   UUID of the player to reset the secret key of
     */
    private void removeKey(UUID uuid) {
        AuthenticationDatabase.cachedKeys.remove(uuid);
        this.database.removeSecretKey(uuid);

        if(Bukkit.getPlayer(uuid) != null)
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GRAY + "Your 2FA was removed!");
    }

    /**
     * Generates a secret key for a player
     *
     * @param player   Player to generate a secret key for
     */
    public void generateKey(Player player) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        AuthenticationDatabase.cachedKeys.put(player.getUniqueId(), key.getKey());

        player.sendMessage(ChatColor.GRAY + "Your 2FA key is: " + ChatColor.YELLOW + key.getKey() + ChatColor.GRAY + "!");
        player.sendMessage(ChatColor.GRAY + "Use it in your authentication app and verify with " + ChatColor.YELLOW + "/2FA <Code>" + ChatColor.GRAY + "!");
    }

    /**
     * Demands a code from a player
     *
     * @param player   Player to demand code from
     */
    public void demandCode(Player player) {
        player.sendMessage(ChatColor.GRAY + "Please verify your identity with " + ChatColor.YELLOW + "/2FA <Code>" + ChatColor.GRAY + "!");
    }
}