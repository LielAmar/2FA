package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.bukkit.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class DisabledEvents implements Listener {

    private final TwoFactorAuthentication plugin;

    public DisabledEvents(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            if(event.getTo() != null && (event.getTo().getBlockZ() != event.getFrom().getBlockZ() || event.getTo().getBlockX() != event.getFrom().getBlockX())) {
                event.setTo(event.getFrom());
                this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else if(this.plugin.getAuthHandler().isQRCodeItem(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else if(this.plugin.getAuthHandler().isQRCodeItem(event.getItem().getItemStack())) {
            event.getItem().remove();
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(this.plugin.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        } else if(event.getCurrentItem() != null && event.getInventory().getType() != InventoryType.PLAYER &&
                (this.plugin.getAuthHandler().isQRCodeItem(event.getCurrentItem()) || this.plugin.getAuthHandler().isQRCodeItem(event.getCursor()))) {
            event.setCancelled(true);
        } else if(event.getHotbarButton() > -1 && this.plugin.getAuthHandler().isQRCodeItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onSlotChange(PlayerItemHeldEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(!this.plugin.getAuthHandler().isQRCodeItem(event.getPlayer().getInventory().getItem(event.getNewSlot()))) {
            if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(this.plugin.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            String[] args = event.getMessage().substring(1).split("\\s+");

            if(this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) {
                if(args.length > 0) {
                    String command = args[0];

                    if(!this.plugin.getConfigHandler().getWhitelistedCommands().contains(command) && !Constants.mainCommand.getA().equalsIgnoreCase(command)) {
                        event.setCancelled(true);
                        this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    } else {
                        try {
                            Integer.parseInt(args[1]);
                        } catch (NumberFormatException exception) {
                            event.setCancelled(true);
                            this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                        }
                    }
                }
            } else {
                if(args.length > 0) {
                    String command = args[0];
                    if(this.plugin.getConfigHandler().getBlacklistedCommands().contains(command)) {
                        event.setCancelled(true);
                        this.plugin.getMessageHandler().sendMessage(event.getPlayer(), MessageHandler.TwoFAMessages.VALIDATE_ACCOUNT);
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemMove(InventoryMoveItemEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(this.plugin.getAuthHandler().isQRCodeItem(event.getItem()) && event.getDestination().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    @SuppressWarnings("deprecation")
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(event.getRightClicked() instanceof ItemFrame) {
            if(this.plugin.getAuthHandler().isQRCodeItem(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        if(!this.plugin.getConfigHandler().getDisabledEvents().getOrDefault(event.getClass(), true)) return;

        if(!this.plugin.getAuthHandler().needsToAuthenticate(event.getEntity().getUniqueId())) return;

        // Don't drop the QR Code item if the player needs to authenticate.
        event.getDrops().removeIf(item -> this.plugin.getAuthHandler().isQRCodeItem(item));
    }
}
