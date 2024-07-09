package com.lielamar.auth.spigot.listeners;

import com.lielamar.auth.api.communication.MessageType;
import com.lielamar.auth.spigot.communication.ProxyCommunicationHandler;
import jakarta.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ProxyAuthDetection implements Listener {
    private final ProxyCommunicationHandler communicationHandler;

    @Inject
    public ProxyAuthDetection(final @NotNull ProxyCommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (!communicationHandler.isConnected()) {
            communicationHandler.sendMessage(MessageType.SPIGOT_SYN, (msg) -> {
                msg.writeUTF(UUID.randomUUID().toString());
            });
            HandlerList.unregisterAll(this);
        }
    }
}
