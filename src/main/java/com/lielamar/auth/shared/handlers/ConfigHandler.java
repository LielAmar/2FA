package com.lielamar.auth.shared.handlers;

import com.lielamar.auth.shared.storage.StorageType;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigHandler {

    private final String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/";

    protected int amountOfReservedMaps;
    protected int[] mapIDs;
    protected String serverName;
    protected String hashType;
    protected StorageType storageType;
    protected boolean requireOnIPChange = true;
    protected boolean requireOnEveryLogin = false;
    protected boolean advice2FA = true;
    protected boolean disableCommands = true;
    protected Map<Class<? extends Event>, Boolean> disabledEvents = new HashMap<>();
    protected List<String> whitelistedCommands = new ArrayList<>();
    protected List<String> blacklistedCommands = new ArrayList<>();

    public int getAmountOfReservedMaps() { return this.amountOfReservedMaps; }
    public int[] getMapIDs() { return this.mapIDs; }
    public String getQrCodeURL() { return this.qrCodeURL; }
    public String getServerName() { return this.serverName; }
    public String getHashType() { return this.hashType; }
    public StorageType getStorageType() { return this.storageType; }
    public boolean isRequiredOnIPChange() {
        return this.requireOnIPChange;
    }
    public boolean isRequiredOnEveryLogin() {
        return this.requireOnEveryLogin;
    }
    public boolean is2FAAdvised() {
        return this.advice2FA;
    }
    public boolean isCommandsDisabled() {
        return this.disableCommands;
    }
    public Map<Class<? extends Event>, Boolean> getDisabledEvents() {
        return this.disabledEvents;
    }
    public List<String> getWhitelistedCommands() {
        return this.whitelistedCommands;
    }
    public List<String> getBlacklistedCommands() {
        return this.blacklistedCommands;
    }

    public abstract void reload();


    protected enum ShorterEvents {
        MOVE(PlayerMoveEvent.class),
        BLOCK_BREAK(BlockBreakEvent.class),
        BLOCK_PLACE(BlockPlaceEvent.class),
        CHAT(AsyncPlayerChatEvent.class),
        DROP(PlayerDropItemEvent.class),
        PICKUP(PlayerPickupItemEvent.class),
        GET_DAMAGE(EntityDamageEvent.class),
        DAMAGE_OTHERS(EntityDamageByEntityEvent.class),
        CLICK_INVENTORY(InventoryClickEvent.class),
        CHANGE_SLOT(PlayerItemHeldEvent.class),
        COMMANDS(PlayerCommandPreprocessEvent.class),
        MOVE_ITEM(InventoryMoveItemEvent.class),
        INTERACT_WITH_FRAMES(PlayerInteractEntityEvent.class);

        private final Class<? extends Event> matchingEvent;

        ShorterEvents(Class<? extends Event> matchingEvent) {
            this.matchingEvent = matchingEvent;
        }

        public Class<? extends Event> getMatchingEvent() {
            return this.matchingEvent;
        }
    }
}