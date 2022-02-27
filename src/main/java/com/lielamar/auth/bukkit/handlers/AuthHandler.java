package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.bukkit.utils.ImageRender;
import com.lielamar.auth.bukkit.utils.Version;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.lielsutils.bukkit.color.ColorUtils;

import com.lielamar.lielsutils.bukkit.map.MapUtils;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthHandler extends com.lielamar.auth.shared.handlers.AuthHandler {

    protected final TwoFactorAuthentication main;

    protected Map<Integer, Long> lastMapIdUse;
    protected Version.ServerVersion version;

    public AuthHandler(TwoFactorAuthentication main) {
        this.main = main;

        super.storageHandler = this.main.getStorageHandler();

        this.lastMapIdUse = new HashMap<>();
        for(int i : main.getConfigHandler().getMapIDs())
            lastMapIdUse.put(i, -1L);

        this.version = Version.getInstance().getServerVersion();

        // Applying 2fa for online players
        // We add a delay to ensure the permission plugin is loaded beforehand
        Bukkit.getScheduler().runTaskLater(this.main, () -> Bukkit.getOnlinePlayers()
                .forEach(player -> this.playerJoin(player.getUniqueId())), Math.min(1, this.main.getConfigHandler().getReloadDelay()));
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
    }


    public void playerJoin(@NotNull UUID uuid) {
//        // Loads the player whenever they join the server.
//        //
//        // If bungeecord was not loaded yet, we want to try to load it. What it generally means is - we send a request called
//        // "LOAD_BUNGEECORD" to bungeecord. If we get a response, it means bungeecord exists, and then we set the value of
//        // isBungeecordEnabled to true, so we know to use bungeecord for future requests.
//        // In case bungeecord was loaded successfully, we provide a callback to re-load the joined player, since when we loaded
//        // them, we did so locally on the spigot instance and not in bungeecord.
//        //
//        // This way, if a player was online and then joined a spigot instance that was just booted, it would load their data and ask
//        // them to authenticate, but straight after that, it'd load bungeecord and then re-load the player and auto-authenticate them.
//
//        handlePlayerJoin(uuid);
//
//        if(!isProxyLoaded) {
//            isProxyLoaded = true;
//
//            long timeMillis = System.currentTimeMillis();
//            Bukkit.getScheduler().runTaskLater(this.main, () -> this.main.getPluginMessageListener().loadBungeecord(uuid, new Callback() {
//                public void execute() { handlePlayerJoin(uuid); }
//                public long getExecutionStamp() { return timeMillis; }
//            }), 1L);
//        }
    }


    public @Nullable String getQRCodeURL(@NotNull String urlTemplate, @NotNull UUID uuid) {
        String encodedPart = "%%label%%?secret=%%key%%&issuer=%%title%%";

        Player player = Bukkit.getPlayer(uuid);

        String label = (player == null) ? "player" : player.getName();
        String title = this.main.getConfigHandler().getServerName();
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
        String url = this.getQRCodeURL(main.getConfigHandler().getQrCodeURL(), player.getUniqueId());

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
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.INVENTORY_FULL_USE_CLICKABLE_MESSAGE);
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
                            main.getMessageHandler().sendClickableMessage(player, MessageHandler.TwoFAMessages.CLICK_TO_OPEN_QR,
                                    url.replaceAll("128x128", "256x256"));

                        if(!MessageHandler.TwoFAMessages.USE_QR_CODE_TO_SETUP_2FA.getMessage().isEmpty())
                            main.getMessageHandler().sendHoverMessage(player, MessageHandler.TwoFAMessages.USE_QR_CODE_TO_SETUP_2FA,
                                    ColorUtils.translateAlternateColorCodes('&', "&7Key: &b" + getPendingKey(player.getUniqueId())));
                    } else {
                        removeQRItem(player);
                        resetKey(player.getUniqueId());
                        main.getMessageHandler().sendMessage(player, MessageHandler.TwoFAMessages.NULL_KEY);
                    }
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
            main.getConfig().set("Map IDs", main.getConfig().getIntegerList("Map IDs").add((int) MapUtils.getMapID(mapView)));
        } else {
            int mapIdWithLongestTime = -1;
            long currentTimeMillis = System.currentTimeMillis();

            for(int i : lastMapIdUse.keySet()) {
                if(mapIdWithLongestTime == -1 || (currentTimeMillis-lastMapIdUse.get(mapIdWithLongestTime)) < currentTimeMillis-lastMapIdUse.get(i))
                    mapIdWithLongestTime = i;
            }

            mapView = Bukkit.getMap((short) mapIdWithLongestTime);
        }

        if(mapView == null) return null;

        lastMapIdUse.put((int) MapUtils.getMapID(mapView), System.currentTimeMillis());

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
}