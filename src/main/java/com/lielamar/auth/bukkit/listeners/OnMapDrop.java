package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OnMapDrop implements Listener {

    private final TwoFactorAuthentication plugin;

    public OnMapDrop(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> toRemove = new ArrayList<>();

        event.getDrops().stream()
                .filter(this.plugin.getAuthHandler()::isQRCodeItem)
                .forEach(toRemove::add);

        event.getDrops().removeAll(toRemove);
    }
}
