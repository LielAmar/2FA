package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.bukkit.utils.ImageRender;
import com.lielamar.auth.shared.storage.StorageType;
import com.lielamar.auth.shared.storage.json.JSONStorage;
import com.lielamar.auth.shared.storage.mongodb.MongoDBStorage;
import com.lielamar.auth.shared.storage.mysql.MySQLStorage;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.util.*;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    /**
     * This class was originally written by Connor Linfoot (https://github.com/ConnorLinfoot/MC2FA).
     * This class was edited by Liel Amar to add Bungeecord, JSON, MySQL, and MongoDB support.
     */

    protected final Main main;
    private final boolean isBungeecord;

    public AuthHandler(Main main) {
        this.main = main;
        this.isBungeecord = SpigotConfig.bungee && !Bukkit.getServer().getOnlineMode();

        loadStorageHandler();
    }

    @Override
    public String createKey(UUID uuid) {
        String key = super.createKey(uuid);

        // If the player is online it gives them a QR code
        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
            giveQRCodeItem(player);

        return key;
    }

    @Override
    public boolean validateKey(UUID uuid, Integer code) {
        boolean valid = super.validateKey(uuid, code);

        if(!valid) {
            int failedAttempts = getFailedAttempts(uuid) + 1;
            setFailedAttempts(uuid, failedAttempts);

            if(failedAttempts > MAX_ATTEMPTS) {
                Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission("2fa.alert") && !needsToAuthenticate(pl.getUniqueId())).forEach(pl ->
                        main.getMessageHandler().sendMessage(pl, "&c%name% failed to authenticate %times% times"
                                .replaceAll("%name%", Bukkit.getPlayer(uuid).getName()).replaceAll("%times%", failedAttempts + "")));
            }
        } else {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()) {
                removeQRItem(player);
                String ip = player.getAddress().getAddress().getHostAddress();
                getStorageHandler().setIP(uuid, ip);
            }
        }

        return valid;
    }

    @Override
    public boolean approveKey(UUID uuid, Integer code) {
        boolean approved = super.approveKey(uuid, code);

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline()) {
            removeQRItem(player);
            String ip = player.getAddress().getAddress().getHostAddress();
            getStorageHandler().setIP(uuid, ip);
        }

        return approved;
    }

    @Override
    public void playerJoin(UUID uuid) {
        super.playerJoin(uuid);

        Player player = Bukkit.getPlayer(uuid);
        if(player == null || !player.isOnline()) {
            return;
        }

        // If the player doesn't have permission to use 2fa
        if(!player.hasPermission("2fa.use")) {
            changeState(uuid, AuthState.DISABLED);
            return;
        }

        // If the player has a key, wait for an authentication, otherwise, set them as 2fa disabled
        if(getStorageHandler().getKey(uuid) != null) {
            changeState(uuid, AuthState.PENDING_LOGIN);
        } else {
            changeState(uuid, AuthState.DISABLED);
        }

        // If the player has a key and they are waiting to authenticate, attempt to auto-authenticate them through BungeeCord/IP
        if(needsToAuthenticate(uuid)) {
            attemptAutoAuthentication(player);
        } else {
            if(player.hasPermission("2fa.demand")) {
                main.getMessageHandler().sendMessage(player, "&6You are required to enable 2FA!");
                createKey(uuid);
            } else {
                if(main.getConfigHandler().is2FAAdvised()) {
                    main.getMessageHandler().sendMessage(player, "&6This server supports two-factor authentication and is highly recommended");
                    main.getMessageHandler().sendMessage(player, "&6Get started by running \"/2fa enable\"");
                }
            }
        }
    }

    @Override
    public void changeState(UUID uuid, AuthState authState) {
        if(authState == getAuthState(uuid))
            return;

        Player player = Bukkit.getPlayer(uuid);
        if(player != null) {
            PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, authState);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled())
                return;

            authState = event.getAuthState();
        }

        authStates.put(uuid, authState);

        if(isBungeecord) {
            if(getAuthState(uuid) == AuthHandler.AuthState.AUTHENTICATED) {
                main.getPluginMessageListener().setBungeeCordAuthenticated(uuid, true);
            }
        }
    }

    @Override
    public String getQRCodeURL(String urlTemplate, UUID uuid) {
        String label;
        Player player = Bukkit.getPlayer(uuid);
        if(player != null) {
            label = main.getConfigHandler().getServerName() + " (" + player.getName() + ")";
        } else {
            label = main.getConfigHandler().getServerName();
        }
        label = label.replaceAll(" ", "%20");
        return super.getQRCodeURL(urlTemplate, uuid).replaceAll("%%label%%", label);
    }


    /**
     * Loads the storage handler (JSON, MySQL, MongoDB)
     *
     */
    private void loadStorageHandler() {
        StorageType storageType = main.getConfigHandler().getStorageType();

        if(storageType == StorageType.MYSQL) {
            ConfigurationSection mysql = main.getConfig().getConfigurationSection("MySQL");
            String host = mysql.getString("credentials.host");
            String database = mysql.getString("credentials.database");
            String username = mysql.getString("credentials.auth.username");
            String password = mysql.getString("credentials.auth.password");
            int port = mysql.getInt("credentials.port");

            this.storageHandler = new MySQLStorage(host, database, username, password, port);
        } else if(storageType == StorageType.MONGODB) {
            ConfigurationSection mysql = main.getConfig().getConfigurationSection("MongoDB");
            String uri = mysql.getString("credentials.uri");
            String host = mysql.getString("credentials.host");
            String database = mysql.getString("credentials.database");
            String username = mysql.getString("credentials.auth.username");
            String password = mysql.getString("credentials.auth.password");
            int port = mysql.getInt("credentials.port");
            boolean requiredAuth = mysql.getBoolean("credentials.auth.required");

            this.storageHandler = new MongoDBStorage(uri, host, database, username, password, port, requiredAuth);
        } else {
            this.storageHandler = new JSONStorage(this.main.getDataFolder().getAbsolutePath());
        }
    }

    /**
     * Attempts to auto authenticate the player on Spigot (using their IP address).
     * If fails, it locks the player
     *
     * @param player   Player to auto authentication
     */
    public void attemptAutoAuthentication(Player player) {
        if(isBungeecord)
            bungeeAutoAuthentication(player);
        else
            spigotAutoAuthentication(player);
    }

    /**
     * Sends a clickable message to a player
     *
     * @param player    Player to send message to
     * @param message   Message to send
     * @param url       URL to open on click
     */
    public void sendClickableMessage(Player player, String message, String url) {
        TextComponent component = new TextComponent(message);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        player.spigot().sendMessage(component);
    }

    /**
     * Gives a player a Map item with the QR code if their code
     *
     * @param player   Player to give item to
     */
    public void giveQRCodeItem(Player player) {
        String url = getQRCodeURL(main.getConfigHandler().getQrCodeURL(), player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                MapView view = Bukkit.createMap(player.getWorld());
                view.getRenderers().forEach(view::removeRenderer);
                try {
                    ImageRender renderer = new ImageRender(url);
                    view.addRenderer(renderer);

                    @SuppressWarnings("deprecation")
                    ItemStack mapItem = new ItemStack(Material.MAP, 1, view.getId());
                    ItemMeta mapMeta = mapItem.getItemMeta();
                    mapMeta.setDisplayName(ChatColor.GRAY + "QR Code");
                    mapItem.setItemMeta(mapMeta);


                    // If inventory is not full
                    if(player.getInventory().firstEmpty() != -1) {
                        ItemStack oldItem = null;

                        if(player.getInventory().firstEmpty() != 0)
                            oldItem = player.getInventory().getItem(0);
                        player.getInventory().setItem(0, mapItem);

                        if(oldItem != null)
                            player.getInventory().addItem(oldItem);
                        player.getInventory().setHeldItemSlot(0);
                    }

                    sendClickableMessage(player, main.getMessageHandler().getMessage("&aPlease click here to open the QR code"), url.replaceAll("128x128", "256x256"));
                    main.getMessageHandler().sendMessage(player, "&aPlease use the QR code given to setup two-factor authentication");
                    main.getMessageHandler().sendMessage(player, "&aPlease validate by entering your code: /2fa <code>");
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                }
            }
        }.runTaskAsynchronously(main);
    }

    /**
     * Removes all QR Code items from the player's inventory
     *
     * @param player   Player to remove QR Code items of
     */
    public void removeQRItem(Player player) {
        player.getInventory().forEach(itemStack -> {
            if(isQRCodeItem(itemStack))
                player.getInventory().remove(itemStack);
        });
    }

    /**
     * Checks if the provided item is a QR item
     *
     * @param item   Item to check
     * @return       Whether it's a QR item
     */
    public boolean isQRCodeItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "QR Code");
    }


    public void bungeeAutoAuthentication(Player player) {
        if(!needsToAuthenticate(player.getUniqueId())) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                main.getPluginMessageListener().isBungeeCordAuthenticated(player.getUniqueId());
            }
        }.runTaskLater(main, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                spigotAutoAuthentication(player);
            }
        }.runTaskLater(main, 2L);
    }

    public void spigotAutoAuthentication(Player player) {
        if(!needsToAuthenticate(player.getUniqueId())) return;

        player.setWalkSpeed(0);
        player.setFlySpeed(0);

        boolean hasIPChanged = main.getAuthHandler().getStorageHandler().getIP(player.getUniqueId()) == null
                || !main.getAuthHandler().getStorageHandler().getIP(player.getUniqueId()).equalsIgnoreCase(player.getAddress().getHostString());

        boolean isRequiredDueToIPChange = main.getConfigHandler().isRequiredOnIPChange() && hasIPChanged;
        boolean isRequiredOnEveryJoin = main.getConfigHandler().isRequiredOnEveryLogin();

        if(!isRequiredDueToIPChange && !isRequiredOnEveryJoin) {
            main.getAuthHandler().changeState(player.getUniqueId(), AuthState.AUTHENTICATED);
            main.getMessageHandler().sendMessage(player, "&aYou were authenticated automatically");
        } else {
            main.getMessageHandler().sendMessage(player, "&cTwo-factor authentication is enabled on this account");
            main.getMessageHandler().sendMessage(player, "&cPlease authenticate using /2fa <code>");
        }
    }
}