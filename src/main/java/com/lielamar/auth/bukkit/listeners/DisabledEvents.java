package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.Main;
import com.lielamar.auth.shared.utils.Constants;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

public class DisabledEvents implements Listener {

    private final Main main;
    public DisabledEvents(Main main) {
        this.main = main;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            if(event.getTo() != null && (event.getTo().getBlockZ() != event.getFrom().getBlockZ() || event.getTo().getBlockX() != event.getFrom().getBlockX())) {
                event.setTo(event.getFrom());
                main.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            main.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            main.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            main.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else if(this.main.getAuthHandler().isQRCodeItem(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else if(this.main.getAuthHandler().isQRCodeItem(event.getItem().getItemStack())) {
            event.getItem().remove();
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(this.main.getAuthHandler().needsToAuthenticate(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        } else if(event.getCurrentItem() != null && event.getInventory().getType() != InventoryType.PLAYER &&
                (this.main.getAuthHandler().isQRCodeItem(event.getCurrentItem()) || this.main.getAuthHandler().isQRCodeItem(event.getCursor()))) {
            event.setCancelled(true);
        } else if(event.getHotbarButton() > -1 && this.main.getAuthHandler().isQRCodeItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(this.main.getAuthHandler().needsToAuthenticate(event.getPlayer().getUniqueId())) {
            String[] args = event.getMessage().substring(1).split("\\s+");
            if(this.main.getConfigHandler().isCommandsDisabled()) {
                if(args.length > 0) {
                    String command = args[0];
                    if(!this.main.getConfigHandler().getWhitelistedCommands().contains(command) && !Constants.mainCommand.equalsIgnoreCase(command)) {
                        event.setCancelled(true);
                        main.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
                    }
                }
            } else {
                if(args.length > 0) {
                    String command = args[0];
                    if(this.main.getConfigHandler().getBlacklistedCommands().contains(command)) {
                        event.setCancelled(true);
                        main.getMessageHandler().sendMessage(event.getPlayer(), "&cPlease validate your account with two-factor authentication");
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemMove(InventoryMoveItemEvent event) {
        if(this.main.getAuthHandler().isQRCodeItem(event.getItem()) && event.getDestination().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    @SuppressWarnings("deprecation")
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
            if(this.main.getAuthHandler().isQRCodeItem(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
            }
        }
    }
}