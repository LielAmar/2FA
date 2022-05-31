package com.lielamar.auth.bukkit.handlers;

import com.lielamar.lielsutils.bukkit.color.ColorUtils;
import com.lielamar.lielsutils.bukkit.files.FileManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private static boolean PLACEHOLDER_API_ENABLED;

    private final FileManager.Config config;

    public MessageHandler(FileManager fileManager) {
        PLACEHOLDER_API_ENABLED = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        this.config = fileManager.getConfig(super.messagesFileName);

        this.reload();
    }

    @Override
    protected void sendRaw(Object sender, String message) {
        if (sender instanceof CommandSender) {
            if (PLACEHOLDER_API_ENABLED && sender instanceof Player) {
                message = PlaceholderAPI.setPlaceholders((Player) sender, message);
            }

            ((CommandSender) sender).sendMessage(ColorUtils.translateAlternateColorCodes('&', message));
        }
    }

    public void sendClickableMessage(Player player, TwoFAMessages message, String clickAction) {
        String rawMessage = message.getMessage();
        String rawPrefix = TwoFAMessages.PREFIX.getMessage();

        String finalMessage = ColorUtils.translateAlternateColorCodes('&', rawPrefix + rawMessage);

        TextComponent component = new TextComponent(finalMessage);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickAction));
        player.spigot().sendMessage(component);
    }

    public void sendHoverMessage(Player player, TwoFAMessages message, String hoverAction) {
        String rawMessage = message.getMessage();
        String rawPrefix = TwoFAMessages.PREFIX.getMessage();

        String finalMessage = ColorUtils.translateAlternateColorCodes('&', rawPrefix + rawMessage);

        TextComponent component = new TextComponent(finalMessage);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverAction).create()));
        player.spigot().sendMessage(component);
    }

    @Override
    public void reload() {
        this.config.reloadConfig();

        for (TwoFAMessages message : TwoFAMessages.values()) {
            if (!this.config.contains(message.name())) {
                this.config.set(message.name(), message.getMessage());
            } else {
                message.setMessage(this.config.getString(message.name()));
            }
        }

        this.saveConfiguration();
    }

    @Override
    public void saveConfiguration() {
        this.config.saveConfig();
    }
}
