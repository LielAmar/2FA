package com.lielamar.auth.shared.handlers;

import com.lielamar.auth.shared.storage.StorageMethod;
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

    protected final String configFileName = "config.yml";


    protected String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/";

    protected boolean checkForUpdates = true;

    protected String serverName = "MyServer";

    protected boolean advise2FA = true;

    protected int reservedMaps = 20;
    protected int[] mapIDs = new int[reservedMaps];

    protected String ipHashType = "SHA256";

    protected long reloadDelay = 0;

    protected Map<Class<? extends Event>, Boolean> disabledEvents = new HashMap<>();
    protected List<String> whitelistedCommands = new ArrayList<>();
    protected List<String> blacklistedCommands = new ArrayList<>();

    protected boolean requireOnIPChange = true;
    protected boolean requireOnEveryLogin = false;

    protected StorageMethod storageMethod = StorageMethod.JSON;

    protected String host = "localhost";
    protected int port = -1;
    protected String database = "auth";
    protected String username = "root";
    protected String password = "password";

    protected String tablePrefix = "2fa_";
    protected String collectionPrefix = "2fa_";

    protected int maximumPoolSize = 10;
    protected int minimumIdle = 10;
    protected int maximumLifetime = 1800000;
    protected int keepAliveTime = 0;
    protected int connectionTimeout = 5000;

    protected String mongodbURI = "";




    public String getQrCodeURL() { return this.qrCodeURL; }

    public boolean shouldCheckForUpdates() { return this.checkForUpdates; }

    public String getServerName() { return this.serverName; }

    public boolean shouldAdvise2FA() { return this.advise2FA; }

    public int getAmountOfReservedMaps() { return this.reservedMaps; }
    public int[] getMapIDs() { return this.mapIDs; }

    public String getIpHashType() { return this.ipHashType; }

    public long getReloadDelay() { return this.reloadDelay; }

    public Map<Class<? extends Event>, Boolean> getDisabledEvents() { return this.disabledEvents; }
    public List<String> getWhitelistedCommands() { return this.whitelistedCommands; }
    public List<String> getBlacklistedCommands() { return this.blacklistedCommands; }

    public boolean shouldRequiredOnIPChange() {
        return this.requireOnIPChange;
    }
    public boolean shouldRequiredOnEveryLogin() {
        return this.requireOnEveryLogin;
    }

    public StorageMethod getStorageMethod() { return this.storageMethod; }

    public String getHost() { return this.host; }
    public int getPort() { return this.port; }
    public String getDatabase() { return this.database; }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    public String getTablePrefix() { return this.tablePrefix; }
    public String getCollectionPrefix() { return this.collectionPrefix; }

    public int getMaximumPoolSize() { return this.maximumPoolSize; }
    public int getMinimumIdle() { return this.minimumIdle; }
    public int getMaximumLifetime() { return this.maximumLifetime; }
    public int getKeepAliveTime() { return this.keepAliveTime; }
    public int getConnectionTimeout() { return this.connectionTimeout; }


    public String getMongodbURI() { return this.mongodbURI; }


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