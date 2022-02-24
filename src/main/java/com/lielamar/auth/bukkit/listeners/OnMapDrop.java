package com.lielamar.auth.bukkit.listeners;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OnMapDrop implements Listener {

    private final TwoFactorAuthentication main;

    public OnMapDrop(TwoFactorAuthentication main) {
        this.main = main;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> toRemove = new ArrayList<>();

        for(ItemStack item : event.getDrops()) {
            if(main.getAuthHandler().isQRCodeItem(item))
                toRemove.add(item);
        }

        event.getDrops().removeAll(toRemove);
    }
}
