package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.bukkit.utils.ImageRender;
import com.lielamar.auth.shared.handlers.Callback;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.storage.StorageType;
import com.lielamar.auth.shared.storage.json.JSONStorage;
import com.lielamar.auth.shared.storage.mongodb.MongoDBStorage;
import com.lielamar.auth.shared.storage.mysql.MySQLStorage;
import com.lielamar.auth.shared.utils.hash.Hash;
import com.lielamar.auth.shared.utils.hash.NoHash;
import com.lielamar.auth.shared.utils.hash.SHA256;
import com.lielamar.auth.shared.utils.hash.SHA512;
import com.lielamar.lielsutils.SpigotUtils;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    /**
     * This class was originally written by Connor Linfoot (https://github.com/ConnorLinfoot/MC2FA).
     * This class was edited by Liel Amar to add Bungeecord, JSON, MySQL, and MongoDB support.
     */

    protected final TwoFactorAuthentication main;
    protected final ExtraAuthHandler extraAuthHandler;

    protected Map<Integer, Long> lastMapIdUse;

    protected int version;
    protected Hash hash;

    public AuthHandler(TwoFactorAuthentication main) {
        this.main = main;
        this.extraAuthHandler = new ExtraAuthHandler();

        this.lastMapIdUse = new HashMap<>();
        for(int i : main.getConfigHandler().getMapIDs()) lastMapIdUse.put(i, -1L);

        this.version = SpigotUtils.getVersion(Bukkit.getVersion().split("MC: 1.")[1]);

        loadHashType();
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

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline()) {
            if(valid) {
                removeQRItem(player);
                updatePlayerIP(player);
            }
        }

        return valid;
    }

    @Override
    public boolean approveKey(UUID uuid, Integer code) {
        boolean approved = super.approveKey(uuid, code);

        if(approved) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                removeQRItem(player);
                updatePlayerIP(player);
            }
        }

        return approved;
    }

    @Override
    public void playerJoin(UUID uuid) {
        super.playerJoin(uuid);

        // The reason there's a 1 tick delay before every message is that if you send a ChannelMessage too fast, sometimes bungeecord doesn't register the message.
        Bukkit.getScheduler().runTaskLater(this.main, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()) {
                return;
            }

            // Removing previous QR codes
            for(ItemStack item : player.getInventory().getContents()) {
                if(isQRCodeItem(item))
                    player.getInventory().remove(item);
            }

            long currentTimestamp = System.currentTimeMillis();

            // Loading the player auth state from bungeecord, and continuing only after it had finished
            extraAuthHandler.loadPlayerAuthState(player, new Callback() {
                public void execute() {
                    // If the loaded AuthState is Authenticated, meaning the player authenticated previously, we want to break.
                    if(getAuthState(player.getUniqueId()) == AuthState.AUTHENTICATED) return;

                    // If the player doesn't have permission to use 2fa
                    if(!player.hasPermission("2fa.use")) {
                        changeState(uuid, AuthState.DISABLED);
                        return;
                    }

                    // If the player has a key, wait for an authentication, otherwise, set them as 2fa disabled
                    changeState(uuid, (getStorageHandler().getKey(uuid) == null ? AuthState.DISABLED : AuthState.PENDING_LOGIN));

                    // we want to check whether the player needs to authenticate or not (if they have a key or not).
                    // If not, we want to either advise them or demand from them to setup 2fa.
                    if(!needsToAuthenticate(player.getUniqueId())) {
                        if(player.hasPermission("2fa.demand")) {
                            main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.YOU_ARE_REQUIRED);
                            createKey(player.getUniqueId());
                            changeState(player.getUniqueId(), AuthState.DEMAND_SETUP);
                        } else {
                            if(main.getConfigHandler().is2FAAdvised()) {
                                main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SETUP_RECOMMENDATION);
                                main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.GET_STARTED);
                            }
                        }
                    // Else, we will try to auto authenticate them
                    } else {
                        extraAuthHandler.autoAuthenticate(player);
                    }
                }
                public long getExecutionStamp() { return currentTimestamp; }
            });
        }, 1L);
    }

    @Override
    public void changeState(UUID uuid, AuthState authState) {
        changeState(uuid, authState, true);
    }

    public void changeState(UUID uuid, AuthState authState, boolean updateBungeecord) {
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

        if(player != null && updateBungeecord)
            extraAuthHandler.updatePlayersBungeecordAuthState(player);
    }

    @Override
    public String getQRCodeURL(String urlTemplate, UUID uuid) {
        String encodedPart = "%%label%%?secret=%%key%%&issuer=%%title%%";

        Player player = Bukkit.getPlayer(uuid);
        String label = (player == null) ? "player" : player.getName();
        String title = main.getConfigHandler().getServerName();
        String key = getPendingKey(uuid);
        encodedPart = encodedPart.replaceAll("%%label%%", label).replaceAll("%%title%%", title).replaceAll("%%key%%", key);

        try {
            return urlTemplate + URLEncoder.encode(encodedPart, StandardCharsets.UTF_8.toString());
        } catch(UnsupportedEncodingException exception) {
            return urlTemplate + encodedPart;
        }
    }


    /**
     * Loads the hash type (SHA-256, SHA-512, No Hash)
     */
    private void loadHashType() {
        String hashType = main.getConfigHandler().getHashType();

        switch (hashType.toUpperCase()) {
            case "SHA256":
                this.hash = new SHA256();
                break;
            case "SHA512":
                this.hash = new SHA512();
                break;
            default:
                this.hash = new NoHash();
                break;
        }
    }

    /**
     * Loads the storage handler (JSON, MySQL, MongoDB)
     */
    private void loadStorageHandler() {
        StorageType storageType = main.getConfigHandler().getStorageType();

        if(storageType == StorageType.MYSQL) {
            ConfigurationSection mysql = main.getConfig().getConfigurationSection("MySQL");

            if(mysql != null) {
                String host = mysql.getString("credentials.host");
                String database = mysql.getString("credentials.database");
                String username = mysql.getString("credentials.auth.username");
                String password = mysql.getString("credentials.auth.password");
                int port = mysql.getInt("credentials.port");

                this.storageHandler = new MySQLStorage(host, database, username, password, port);
            }
        } else if(storageType == StorageType.MONGODB) {
            ConfigurationSection mongodb = main.getConfig().getConfigurationSection("MongoDB");

            if(mongodb != null) {
                String uri = mongodb.getString("credentials.uri");
                String host = mongodb.getString("credentials.host");
                String database = mongodb.getString("credentials.database");
                String username = mongodb.getString("credentials.auth.username");
                String password = mongodb.getString("credentials.auth.password");
                int port = mongodb.getInt("credentials.port");
                boolean requiredAuth = mongodb.getBoolean("credentials.auth.required");

                this.storageHandler = new MongoDBStorage(uri, host, database, username, password, port, requiredAuth);
            }
        }

        if(this.storageHandler == null) {
            this.storageHandler = new JSONStorage(this.main.getDataFolder().getAbsolutePath());
        }
    }


    /**
     * Updates a player's ip in the database
     *
     * @param player   Player to update the ip of
     */
    public void updatePlayerIP(Player player) {
        if(player.getAddress() != null && player.getAddress().getAddress() != null) {
            String ip = this.hash.hash(player.getAddress().getAddress().getHostAddress());
            getStorageHandler().setIP(player.getUniqueId(), ip);
        }
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
    @SuppressWarnings("deprecation")
    public void giveQRCodeItem(Player player) {
        String url = getQRCodeURL(main.getConfigHandler().getQrCodeURL(), player.getUniqueId());

        if(url == null)
            return;

        new BukkitRunnable() {
            final MapView view = getMap(player.getWorld());

            @Override
            public void run() {
                view.getRenderers().forEach(view::removeRenderer);

                try {
                    ImageRender renderer = new ImageRender(url);
                    view.addRenderer(renderer);

                    ItemStack mapItem = new ItemStack(Material.MAP);
                    if(version >= 13) {
                        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
                        if(mapMeta != null) {
                            mapMeta.setMapId(view.getId());
                            mapItem.setItemMeta(mapMeta);
                        }
                    } else {
                        mapItem = new ItemStack(Material.MAP, 1, SpigotUtils.getMapID(view));
                    }

                    ItemMeta meta = mapItem.getItemMeta();
                    if(meta != null) {
                        meta.setDisplayName(ChatColor.GRAY + "QR Code");
                        mapItem.setItemMeta(meta);
                    }

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

                    sendClickableMessage(player, ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.PREFIX.getMessage() + MessageHandler.TwoFAMessages.CLICK_TO_OPEN_QR.getMessage()), url.replaceAll("128x128", "256x256"));
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.USE_QR_CODE_TO_SETUP_2FA);
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.PLEASE_AUTHENTICATE);
                } catch (IOException | NumberFormatException exception) {
                    exception.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                }
            }
        }.runTaskAsynchronously(main);
    }

    /**
     * If we have X map ids where X is smaller than the amount of maps assigned to the plugin, the plugin will create a new map and save its ID in the config for future use.
     * If X is equals to or greater than the amount of maps assigned, we get the map with the longest time passed since it was reassigned.
     *
     * @param world   World to create the map in
     * @return        Created/Assigned MapView object
     */
    @SuppressWarnings("deprecation")
    public MapView getMap(World world) {
        MapView mapView;

        if(lastMapIdUse.size() < main.getConfigHandler().getAmountOfReservedMaps()) {
            mapView = Bukkit.createMap(world);
            main.getConfig().set("Map IDs", main.getConfig().getIntegerList("Map IDs").add((version >= 13) ? mapView.getId() : SpigotUtils.getMapID(mapView)));
        } else {
            int mapIdWithLongestTime = -1;
            long currentTimeMillis = System.currentTimeMillis();

            for(int i : lastMapIdUse.keySet()) {
                if(mapIdWithLongestTime == -1 || (currentTimeMillis-lastMapIdUse.get(mapIdWithLongestTime)) < currentTimeMillis-lastMapIdUse.get(i))
                    mapIdWithLongestTime = i;
            }

            mapView = Bukkit.getMap(mapIdWithLongestTime);
        }

        if(mapView == null) return null;

        lastMapIdUse.put((version >= 13) ? mapView.getId() : SpigotUtils.getMapID(mapView), System.currentTimeMillis());

        return mapView;
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
        return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "QR Code");
    }


    /**
     * Inner class to handle BungeeCord communication
     */
    public class ExtraAuthHandler {

        private final boolean isBungeecord;

        public ExtraAuthHandler() {
            this.isBungeecord = SpigotConfig.bungee && !Bukkit.getServer().getOnlineMode();
        }

        /**
         * Updates the player's AuthState on the Bungeecord Server
         *
         * @param player   Player to update BungeeCord AuthState of
         */
        public void updatePlayersBungeecordAuthState(Player player) {
            if(isBungeecord)
                main.getPluginMessageListener().setBungeeCordAuthState(player.getUniqueId(), getAuthState(player.getUniqueId()));
        }

        /**
         * Loads the player's AuthState from the BungeeCord server
         *
         * @param player     Player to load BungeeCord AuthState of
         * @param callback   Callback function to execute after the AuthState is fully loaded.
         */
        public void loadPlayerAuthState(Player player, Callback callback) {
            if(isBungeecord) {
                AuthState defaultState = getAuthState(player.getUniqueId());
                main.getPluginMessageListener().getBungeeCordAuthState(player.getUniqueId(), (defaultState == null ? AuthState.DISABLED : defaultState), callback);
            }
        }

        /**
         * Auto authenticates the player through either Bungeecord or Spigot.
         *
         * @param player   Player to auto authenticate
         */
        public void autoAuthenticate(Player player) {
            if(isBungeecord) bungeecordAutoAuthenticate(player);
            else spigotAutoAuthenticate(player);
        }

        private void bungeecordAutoAuthenticate(Player player) {
            if(!needsToAuthenticate(player.getUniqueId())) return;

            final long currentTimestamp = System.currentTimeMillis();
            main.getPluginMessageListener().getBungeeCordAuthState(player.getUniqueId(), getAuthState(player.getUniqueId()), new Callback() {
                public void execute() { spigotAutoAuthenticate(player); }
                public long getExecutionStamp() { return currentTimestamp; }
            });
        }

        private void spigotAutoAuthenticate(Player player) {
            if(!needsToAuthenticate(player.getUniqueId())) return;

            if(player.getAddress() != null && player.getAddress().getAddress() != null) {
                String ip = hash.hash(player.getAddress().getAddress().getHostAddress());

                boolean hasIPChanged = getStorageHandler().getIP(player.getUniqueId()) == null
                        || !getStorageHandler().getIP(player.getUniqueId()).equalsIgnoreCase(ip);

                boolean isRequiredDueToIPChange = main.getConfigHandler().isRequiredOnIPChange() && hasIPChanged;
                boolean isRequiredOnEveryJoin = main.getConfigHandler().isRequiredOnEveryLogin();

                if(!isRequiredDueToIPChange && !isRequiredOnEveryJoin) {
                    changeState(player.getUniqueId(), AuthState.AUTHENTICATED);
                    main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.AUTHENTICATED_AUTOMATICALLY);
                    return;
                }
            }

            player.setWalkSpeed(0);
            player.setFlySpeed(0);

            main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.TWOFA_IS_ENABLED);
            main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.PLEASE_AUTHENTICATE);
        }
    }
}