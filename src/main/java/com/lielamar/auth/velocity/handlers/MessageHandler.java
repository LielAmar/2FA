package com.lielamar.auth.velocity.handlers;

import com.lielamar.auth.velocity.TwoFactorAuthentication;
import com.lielamar.lielsutils.groups.Pair;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class MessageHandler extends com.lielamar.auth.shared.handlers.MessageHandler {

    private final TwoFactorAuthentication plugin;

    public MessageHandler(@NotNull TwoFactorAuthentication plugin) {
        this.plugin = plugin;

        this.reload();
    }


    @Override
    protected void sendRaw(Object player, String message) {
        this.sendRaw(player, message, TextColor.fromHexString("#ffffff"));
    }

    protected void sendRaw(final Object player, final String message, TextColor color) {
        if(player instanceof Player)
            Audience.audience((Player) player).sendMessage(
                    Component.text().content(message).color(color).build(),
                    MessageType.CHAT);
    }


    public void sendMessage(Object sender, TwoFAMessages message) {
        this.sendMessage(sender, (TwoFAMessages.PREFIX.getMessage().length() > 0), message);
    }

    protected void sendMessage(Object sender, boolean prefix, TwoFAMessages message, Pair<?, ?>... args) {
        String raw = message.getMessage();
        String rawPrefix = TwoFAMessages.PREFIX.getMessage();

        if(raw != null && raw.length() > 0) {
            for(Pair<?, ?> pair : args)
                raw = raw.replaceAll(pair.getA().toString(), pair.getB().toString());

            this.sendRaw(sender, (prefix ? rawPrefix : "") + raw, message.getColor());
        }
    }

    @Override
    public void reload() {
        if(!this.plugin.getDataDirectory().toFile().exists())
            this.plugin.getDataDirectory().toFile().mkdir();

        File file = new File(this.plugin.getDataDirectory().toFile(), "messages.toml");

        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if(!file.exists()) {
            try(InputStream input = getClass().getResourceAsStream("/velocitymessages.toml")) {
                if(input != null)
                    Files.copy(input, file.toPath());
                else
                    file.createNewFile();
            } catch(IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        Toml config = new Toml().read(file);
        Toml messages = config.getTable("messages");

        for(TwoFAMessages message : TwoFAMessages.values()) {
            if(messages.contains(message.name())) {
                List<String> msg = messages.getList(message.name());
                message.setMessage(msg.get(0));
                message.setColor(msg.get(1));
            }
        }
    }

    @Override
    public void saveConfiguration() {}


    public enum TwoFAMessages {
        PREFIX("[2FA] ", "b"),
        VALIDATE_ACCOUNT("Please validate your account with two-factor authentication", "#ff0000");

        public String message;
        public TextColor color;

        TwoFAMessages(String message, String color) {
            this.message = message;

            setColor(color);
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public TextColor getColor() {
            return color;
        }

        public void setColor(String color) {
            if(color.equalsIgnoreCase("0")) color = "#000000";
            if(color.equalsIgnoreCase("1")) color = "#0000AA";
            if(color.equalsIgnoreCase("2")) color = "#00AA00";
            if(color.equalsIgnoreCase("3")) color = "#00AAAA";
            if(color.equalsIgnoreCase("4")) color = "#AA0000";
            if(color.equalsIgnoreCase("5")) color = "#AA00AA";
            if(color.equalsIgnoreCase("6")) color = "#FFAA00";
            if(color.equalsIgnoreCase("7")) color = "#AAAAAA";
            if(color.equalsIgnoreCase("8")) color = "#555555";
            if(color.equalsIgnoreCase("9")) color = "#5555FF";
            if(color.equalsIgnoreCase("a")) color = "#55FF55";
            if(color.equalsIgnoreCase("b")) color = "#55FFFF";
            if(color.equalsIgnoreCase("c")) color = "#FF5555";
            if(color.equalsIgnoreCase("d")) color = "#FF55FF";
            if(color.equalsIgnoreCase("e")) color = "#FFFF55";
            if(color.equalsIgnoreCase("f")) color = "#FFFFFF";

            this.color = TextColor.fromHexString(color);
        }
    }
}