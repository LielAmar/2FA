package com.lielamar.auth.shared.utils.color;

import net.md_5.bungee.api.ChatColor;

public class ColorUtils {

    /**
     * Colors a Bukkit message with support to hex color codes
     *
     * @param codeChar   char to use to translate color codes
     * @param message    Message to color
     * @return           Colored message
     */
    public static String translateAlternateColorCodes(char codeChar, String message) {
        char[] chars = message.toCharArray();

        StringBuilder builder  = new StringBuilder();
        String colorHex = "";

        boolean isHex = false;

        for(int i = 0; i < chars.length; i++) {
            if(chars[i] == codeChar && i < chars.length - 1 && chars[i+1] == '#'){
                colorHex = "";
                isHex = true;
            } else if(isHex) {
                colorHex += chars[i];
                isHex = colorHex.length() < 7;

                if(!isHex)
                    builder.append(ChatColor.of(colorHex));
            } else
                builder.append(chars[i]);
        }

        return ChatColor.translateAlternateColorCodes(codeChar, builder.toString());
    }
}