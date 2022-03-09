package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.communication.BasicAuthCommunication;
import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.communication.AuthCommunicationHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.utils.hash.Hash;
import com.lielamar.auth.shared.utils.hash.NoHash;
import com.lielamar.auth.shared.utils.hash.SHA256;
import com.lielamar.auth.shared.utils.hash.SHA512;
import com.lielamar.lielsutils.bukkit.color.ColorUtils;

import com.lielamar.lielsutils.bukkit.map.ImageRender;
import com.lielamar.lielsutils.bukkit.map.MapUtils;

import com.lielamar.lielsutils.bukkit.version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    protected final TwoFactorAuthentication plugin;

    protected Map<Integer, Long> lastUsedMapIds;
    protected Version.ServerVersion version;

    protected Hash hash;

    private int callbackTimeouts;

    public AuthHandler(@NotNull TwoFactorAuthentication plugin, @Nullable StorageHandler storageHandler,
                       @Nullable AuthCommunicationHandler authCommunicationHandler, @Nullable AuthCommunicationHandler fallbackCommunicationHandler) {
        super(storageHandler, authCommunicationHandler, fallbackCommunicationHandler);

        this.plugin = plugin;

        this.lastUsedMapIds = new HashMap<>();
        Arrays.stream(plugin.getConfigHandler().getMapIDs()).forEach(i -> lastUsedMapIds.put(i, -1L));

        this.version = Version.getInstance().getServerVersion();

        String hashType = this.plugin.getConfigHandler().getIpHashType().toUpperCase();
        if(hashType.equalsIgnoreCase("SHA256"))      this.hash = new SHA256();
        else if(hashType.equalsIgnoreCase("SHA512")) this.hash = new SHA512();
        else                                                    this.hash = new NoHash();

        this.callbackTimeouts = 0;
    }

    public void reloadOnlinePlayers() {
        // Applying 2fa for online players
        // We add at least 1 tick delay, ensuring the permission plugin is loaded beforehand
        Bukkit.getScheduler().runTaskLater(this.plugin,
                () -> Bukkit.getOnlinePlayers().forEach(player -> this.playerJoin(player.getUniqueId())),
                Math.min(1, this.plugin.getConfigHandler().getReloadDelay()));
    }

    @Override
    public @NotNull String createKey(@NotNull UUID uuid) {
        String key = super.createKey(uuid);

        // If the player is online it gives them a QR code
        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
            giveQRCodeItem(player);

        return key;
    }

    @Override
    public boolean validateKey(@NotNull UUID uuid, @NotNull Integer code) {
        boolean valid = super.validateKey(uuid, code);

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline()) {
            if(valid)
                removeQRItem(player);
        }

        return valid;
    }

    @Override
    public boolean approveKey(@NotNull UUID uuid, @NotNull Integer code) {
        boolean approved = super.approveKey(uuid, code);

        if(approved) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null)
                removeQRItem(player);
        }

        return approved;
    }

    @Override
    public void changeState(@NotNull UUID uuid, @NotNull AuthState authState) {
        if(authState == super.getAuthState(uuid))
            return;

        Player player = Bukkit.getPlayer(uuid);
        if(player != null) {
            PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, getAuthState(uuid), authState);

            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled())
                return;

            authState = event.getNewAuthState();
        }

        authStates.put(uuid, authState);

        // If the PlayerStateChangeEvent was not cancelled we wanna update the auth communication handler
        if(player != null && authState == AuthState.AUTHENTICATED)
            this.authCommunicationHandler.setPlayerState(uuid, authState);
    }


    public void playerJoin(@NotNull UUID uuid) {
        if(super.authCommunicationHandler == null)
            super.authCommunicationHandler = new BasicAuthCommunication(this.plugin);

        // Checking whether we need to retrieve data on the user
        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline())
            return;

        if(this.getStorageHandler() == null)
            return;

        if(!player.hasPermission("2fa.use") || this.getStorageHandler().getKey(uuid) == null) {
            this.changeState(uuid, AuthState.DISABLED);
            return;
        }

        // Setting the initial state so players can't abuse the brief moment without 2fa protection
        this.changeState(uuid, AuthState.PENDING_LOGIN);

        // Asking communication handler to load the player state and execute LoadAuthCallback when a result is given
        super.authCommunicationHandler.loadPlayerState(uuid, new LoadAuthCallback(uuid));
    }


    public @Nullable String getQRCodeURL(@NotNull String urlTemplate, @NotNull UUID uuid) {
        String encodedPart = "%%label%%?secret=%%key%%&issuer=%%title%%";

        Player player = Bukkit.getPlayer(uuid);

        String label = (player == null) ? "player" : player.getName();
        String title = this.plugin.getConfigHandler().getServerName();
        String key = super.getPendingKey(uuid);

        if(key == null)
            return null;

        encodedPart = encodedPart.replaceAll("%%label%%", label).replaceAll("%%title%%", title).replaceAll("%%key%%", key);

        try {
            return urlTemplate + URLEncoder.encode(encodedPart, StandardCharsets.UTF_8.toString());
        } catch(UnsupportedEncodingException exception) {
            return urlTemplate + encodedPart;
        }
    }

    /**
     * Gives a player a Map item with the QR code if their code
     *
     * @param player   Player to give item to
     */
    @SuppressWarnings("deprecation")
    public void giveQRCodeItem(@NotNull Player player) {
        String url = this.getQRCodeURL(this.plugin.getConfigHandler().getQrCodeURL(), player.getUniqueId());

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

                    ItemStack mapItem;

                    if(version.above(Version.ServerVersion.v1_13_0)) {
                        mapItem = new ItemStack(Material.FILLED_MAP);

                        if(mapItem.getItemMeta() instanceof MapMeta) {
                            MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

                            if(mapMeta != null) {
                                mapMeta.setMapId(view.getId());
                                mapItem.setItemMeta(mapMeta);
                            }
                        }
                    } else {
                        mapItem = new ItemStack(Material.MAP, 1, MapUtils.getMapID(view));
                    }

                    ItemMeta meta = mapItem.getItemMeta();
                    if(meta != null) {
                        meta.setDisplayName(ChatColor.GRAY + "QR Code");
                        mapItem.setItemMeta(meta);
                    }

                    // If the inventory is full we don't want to remove any of the player's items, but rather tell them to use the clickable message
                    // to authenticate
                    // otherwise, we want to add the map with the qr code to their first available hotbar slot and move the item in that slot.
                    if(player.getInventory().firstEmpty() == -1) {
                        plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INVENTORY_FULL_USE_CLICKABLE_MESSAGE);
                    } else {
                        int availableFirstSlot = player.getInventory().firstEmpty();

                        if(availableFirstSlot > 8) {
                            ItemStack oldItem = player.getInventory().firstEmpty() != 0 ? player.getInventory().getItem(0) : null;

                            player.getInventory().setItem(0, mapItem);

                            if(oldItem != null)
                                player.getInventory().addItem(oldItem);

                            player.getInventory().setHeldItemSlot(0);
                        } else {
                            player.getInventory().setItem(availableFirstSlot, mapItem);
                            player.getInventory().setHeldItemSlot(availableFirstSlot);
                        }
                    }

                    // If the player's key is not null we want to send him the hover and clickable messages
                    // with the key and the link to the QR image.
                    // otherwise, we would want to completely void the player's key data, remove the QRItem and also send him a message about the issue.
                    if(getPendingKey(player.getUniqueId()) != null) {
                        if(!MessageHandler.TwoFAMessages.CLICK_TO_OPEN_QR.getMessage().isEmpty())
                            plugin.getMessageHandler().sendClickableMessage(player, MessageHandler.TwoFAMessages.CLICK_TO_OPEN_QR,
                                    url.replaceAll("128x128", "256x256"));

                        if(!MessageHandler.TwoFAMessages.USE_QR_CODE_TO_SETUP_2FA.getMessage().isEmpty())
                            plugin.getMessageHandler().sendHoverMessage(player, MessageHandler.TwoFAMessages.USE_QR_CODE_TO_SETUP_2FA,
                                    ColorUtils.translateAlternateColorCodes('&', "&7Key: &b" + getPendingKey(player.getUniqueId())));
                    } else {
                        removeQRItem(player);
                        resetKey(player.getUniqueId());
                        plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.NULL_KEY);
                    }
                } catch (IOException | NumberFormatException exception) {
                    exception.printStackTrace();
                    player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                }
            }
        }.runTaskAsynchronously(this.plugin);
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

        if(lastUsedMapIds.size() < this.plugin.getConfigHandler().getAmountOfReservedMaps()) {
            mapView = Bukkit.createMap(world);
            this.plugin.getConfig().set("Map IDs", this.plugin.getConfig().getIntegerList("Map IDs").add((int) MapUtils.getMapID(mapView)));
        } else {
            int mapIdWithLongestTime = -1;
            long currentTimeMillis = System.currentTimeMillis();

            for(int i : lastUsedMapIds.keySet()) {
                if(mapIdWithLongestTime == -1 || (currentTimeMillis - lastUsedMapIds.get(mapIdWithLongestTime)) < currentTimeMillis - lastUsedMapIds.get(i))
                    mapIdWithLongestTime = i;
            }

            mapView = Bukkit.getMap((short) mapIdWithLongestTime);
        }

        if(mapView == null)
            return null;

        lastUsedMapIds.put((int) MapUtils.getMapID(mapView), System.currentTimeMillis());
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


    private class LoadAuthCallback implements AuthCommunicationHandler.AuthCommunicationCallback {

        private final UUID playerUUID;
        private final long timeMillis;

        public LoadAuthCallback(@NotNull UUID playerUUID) {
            this.playerUUID = playerUUID;
            this.timeMillis = System.currentTimeMillis();
        }

        @Override
        public void execute(AuthState authState) {
            Player player = Bukkit.getPlayer(this.getPlayerUUID());

            if(player == null || !player.isOnline())
                return;

            if(getStorageHandler() == null)
                return;

            // If AuthCommunication's result returned that the player is already authenticated, we don't need to continue
            if(authState == AuthState.AUTHENTICATED || authState == AuthState.NONE) {
                changeState(this.playerUUID, authState);
                return;
            }

            // Otherwise, we either want to disable their 2fa if they don't have it set up, or wait for them to log in.
            changeState(playerUUID, (getStorageHandler().getKey(playerUUID) == null ? AuthState.DISABLED : AuthState.PENDING_LOGIN));

            if(!needsToAuthenticate(player.getUniqueId())) {
                if(player.hasPermission("2fa.demand")) {
                    createKey(player.getUniqueId());
                    changeState(player.getUniqueId(), AuthState.DEMAND_SETUP);

                    plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.YOU_ARE_REQUIRED);
                } else {
                    if(plugin.getConfigHandler().shouldAdvise2FA()) {
                        plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.SETUP_RECOMMENDATION);
                        plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.GET_STARTED);
                    }
                }
            } else
                tryToAutoAuthenticate(player);
        }

        @Override
        public void onTimeout() {
            callbackTimeouts++;
            if(callbackTimeouts > 10) {
                callbackTimeouts = 0;

                Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission("2fa.alerts")).forEach(pl -> {
                    plugin.getMessageHandler().sendMessage(pl, MessageHandler.TwoFAMessages.COMMUNICATION_METHOD_NOT_CORRECT);
                });
            }

            fallbackCommunicationHandler.loadPlayerState(playerUUID, this);
        }

        @Override
        public long getExecutionStamp() {
            return this.timeMillis;
        }

        @Override
        public UUID getPlayerUUID() {
            return playerUUID;
        }

        private void tryToAutoAuthenticate(Player player) {
            if(!needsToAuthenticate(player.getUniqueId()))
                return;

            if(getStorageHandler() == null)
                return;

            if(player.getAddress() != null && player.getAddress().getAddress() != null) {
                String ip = hash.hash(player.getAddress().getAddress().getHostAddress());

                boolean hasIPChanged = getStorageHandler().getIP(player.getUniqueId()) == null
                        || !getStorageHandler().getIP(player.getUniqueId()).equalsIgnoreCase(ip);

                boolean isRequiredDueToIPChange = plugin.getConfigHandler().shouldRequiredOnIPChange() && hasIPChanged;
                boolean isRequiredOnEveryJoin = plugin.getConfigHandler().shouldRequiredOnEveryLogin();

                if(!isRequiredDueToIPChange && !isRequiredOnEveryJoin) {
                    changeState(player.getUniqueId(), AuthState.AUTHENTICATED);
                    plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.AUTHENTICATED_AUTOMATICALLY);
                    return;
                }
            }

            player.setWalkSpeed(0);
            player.setFlySpeed(0);

            plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.TWOFA_IS_ENABLED);
            plugin.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.PLEASE_AUTHENTICATE);
        }
    }
}