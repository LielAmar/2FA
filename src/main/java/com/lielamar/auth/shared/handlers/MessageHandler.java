package com.lielamar.auth.shared.handlers;

import java.util.Arrays;
import java.util.List;

public abstract class MessageHandler {

    /**
     * This class was originally written by Connor Linfoot (https://github.com/ConnorLinfoot/MC2FA).
     * This class was edited by Liel Amar to add Bungeecord, JSON, MySQL, and MongoDB support.
     */

    private String prefix = "&7[&b2FA&7]&r ";
    private final List<String> defaults = Arrays.asList(
            "&cPlease validate your account with two-factor authentication", 
            "&cThe code you entered was not valid, please try again", 
            "&aYou have successfully setup two-factor authentication", 
            "&cThis command must be ran as a player", 
            "&cUsage: /2fa <code>", 
            "&aYou have successfully authenticated", 
            "&cIncorrect code, please try again", 
            "&aYou were authenticated automatically",
            "&cYou are already setup with 2FA", 
            "&aYour 2FA has been reset", 
            "&cYou are not setup with 2FA", 
            "&cYou do not have permission to run this command", 
            "&aPlease use the QR code given to setup two-factor authentication", 
            "&aPlease validate by entering your code: /2fa <code>", 
            "&6This server supports two-factor authentication and is highly recommended", 
            "&6Get started by running \"/2fa enable\"", 
            "&cTwo-factor authentication is enabled on this account", 
            "&cPlease authenticate using /2fa <code>", 
            "&aPlease click here to open the QR code",
            "&a%name%'s 2FA has been reset",
            "&c%name% could not be found", 
            "&c%name% is not setup with 2FA", 
            "&aConfig was reloaded", 
            "&c%name% failed to authenticate %times% times",
            "&cSomething went wrong. Please contact a Staff Member!"
    );

    public String getPrefix() {
        return this.prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public abstract String getMessage(String message);

    public abstract void loadConfiguration();

    public List<String> getDefaults() {
        return defaults;
    }
}
