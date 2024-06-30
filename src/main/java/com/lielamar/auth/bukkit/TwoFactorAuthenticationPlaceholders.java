package com.lielamar.auth.bukkit;

import com.lielamar.auth.bukkit.utils.TimeUtils;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class TwoFactorAuthenticationPlaceholders extends PlaceholderExpansion {

    private final TwoFactorAuthentication plugin;

    public TwoFactorAuthenticationPlaceholders(TwoFactorAuthentication plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "2FA";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        return switch (identifier.toLowerCase()) {
            case "is_enabled" -> plugin.getAuthHandler().is2FAEnabled(player.getUniqueId())
                    ? MessageHandler.TwoFAMessages.KEYWORD_ENABLED.getMessage()
                    : MessageHandler.TwoFAMessages.KEYWORD_DISABLED.getMessage();
            case "time_since_enabled" -> {
                long enableDate = plugin.getAuthHandler().getStorageHandler().getEnableDate(player.getUniqueId());
                yield enableDate == -1 ? "Not Enabled"
                        : TimeUtils.parseTime(System.currentTimeMillis() - enableDate);
            }
            case "key" -> plugin.getAuthHandler().getStorageHandler()
                    .getKey(player.getUniqueId());
            case "is_required" -> player.hasPermission(Constants.demandPermission)
                    ? MessageHandler.TwoFAMessages.KEYWORD_REQUIRED.getMessage()
                    : MessageHandler.TwoFAMessages.KEYWORD_NOT_REQUIRED.getMessage();
            default -> null;
        };

    }
}
