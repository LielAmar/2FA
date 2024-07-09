package com.lielamar.auth.spigot.handlers;

import com.lielamar.auth.api.communication.CommunicationMethod;
import com.lielamar.auth.core.config.AbstractConfigHandler;
import dev.sadghost.espresso.files.FileManager;
import dev.sadghost.espresso.files.IConfig;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;

public final class ConfigHandler extends AbstractConfigHandler {

    private final IConfig config;

    public ConfigHandler(FileManager fileManager) {
        this.config = fileManager.getConfig("config.yml").orElseThrow(() -> new IllegalStateException("Failed to get the configuration."));

        this.reload();
    }

    protected boolean checkForUpdates = true;

    protected String serverName = "My Minecraft Server";
    protected boolean advise2FA = true;

    protected int reservedMaps = 20;
    protected int[] mapIDs = new int[reservedMaps];

    protected long reloadDelay = 0;
    protected String ipHashType = "SHA256";

    protected boolean requireOnIPChange = true;
    protected boolean requireOnEveryLogin = false;

    protected boolean tpBeforeAuth = false;
    protected Location tpBeforeAuthLocation = null;
    protected boolean tpAfterAuth = false;
    protected Location tpAfterAuthLocation = null;

    protected CommunicationMethod communicationMethod = CommunicationMethod.NONE;
    protected int communicationTimeout = 30;

    public boolean shouldCheckForUpdates() {
        return this.checkForUpdates;
    }

    public String getServerName() {
        return this.serverName;
    }

    public boolean shouldAdvise2FA() {
        return this.advise2FA;
    }

    public int getAmountOfReservedMaps() {
        return this.reservedMaps;
    }

    public int[] getMapIDs() {
        return this.mapIDs;
    }

    public long getReloadDelay() {
        return this.reloadDelay;
    }

    public String getIpHashType() {
        return this.ipHashType;
    }

    public boolean shouldRequiredOnIPChange() {
        return this.requireOnIPChange;
    }

    public boolean shouldRequiredOnEveryLogin() {
        return this.requireOnEveryLogin;
    }

    public boolean shouldTeleportBeforeAuth() {
        return this.tpBeforeAuth;
    }

    public Location teleportBeforeAuthLocation() {
        return this.tpBeforeAuthLocation;
    }

    public boolean shouldTeleportAfterAuth() {
        return this.tpAfterAuth;
    }

    public Location teleportAfterAuthLocation() {
        return this.tpAfterAuthLocation;
    }

    public CommunicationMethod getCommunicationMethod() {
        return this.communicationMethod;
    }

    public int getCommunicationTimeout() {
        return this.communicationTimeout;
    }

    @Override
    public void reload() {

    }

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
        INTERACT_WITH_FRAMES(PlayerInteractEntityEvent.class),
        DEATH(PlayerDeathEvent.class);

        private final Class<? extends Event> matchingEvent;

        ShorterEvents(Class<? extends Event> matchingEvent) {
            this.matchingEvent = matchingEvent;
        }

        public Class<? extends Event> getMatchingEvent() {
            return this.matchingEvent;
        }
    }
}
