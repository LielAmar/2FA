package com.lielamar.auth.shared.handlers;

import com.lielamar.lielsutils.modules.Pair;
import org.jetbrains.annotations.NotNull;

public abstract class MessageHandler {

    protected final String messagesFileName = "messages.yml";

    public void sendMessage(Object player, TwoFAMessages message, Pair<?, ?>... args) {
        this.sendMessage(player, true, message, args);
    }

    /**
     * Sends the value of the provided {TwoFAMessage} object to the given player
     *
     * @param player    Player to send the message to
     * @param prefix    Whether to send the prefix as well
     * @param message   TwoFAMessage object to send its value
     * @param args      Array of pairs of arguments to switch in the message
     */
    public void sendMessage(Object player, boolean prefix, TwoFAMessages message, Pair<?, ?>... args) {
        String raw = message.getMessage();
        String rawPrefix = TwoFAMessages.PREFIX.getMessage();

        if(raw != null && raw.length() > 0) {
            for(Pair<?, ?> pair : args)
                raw = raw.replaceAll(pair.getKey().toString(), pair.getValue().toString());

            this.sendRaw(player, (prefix ? rawPrefix : "") + raw);
        }
    }


    protected abstract void sendRaw(final Object p0, final String p1);

    public abstract void reload();

    public abstract void saveConfiguration();

    public enum TwoFAMessages {
        PREFIX("&7[&b2FA&7]&r "),
        VALIDATE_ACCOUNT("&cPlease validate your account with two-factor authentication"),
        INVALID_CODE("&cThe code you entered was not valid, please try again"),
        SUCCESSFULLY_SETUP("&aYou have successfully setup two-factor authentication"),
        MUST_BE_A_PLAYER("&cThis command must be ran as a player"),
        USAGE("&cUsage: /2fa <code>"),
        YOU_ARE_REQUIRED("&6You are required to enable 2FA!"),
        GENERATING_KEY("&aGenerating your 2FA key..."),
        SUCCESSFULLY_AUTHENTICATED("&aYou have successfully authenticated"),
        INCORRECT_CODE("&cIncorrect code, please try again"),
        AUTHENTICATED_AUTOMATICALLY("&aYou were authenticated automatically"),
        ALREADY_IN_SETUP_MODE("&cYou are already in the setup mode for 2FA!"),
        ALREADY_SETUP("&cYou are already setup with 2FA"),
        RESET_2FA("&aYour 2FA has been reset"),
        NOT_SETUP("&cYou are not setup with 2FA"),
        NO_PERMISSIONS("&cYou do not have permission to run this command"),
        USE_QR_CODE_TO_SETUP_2FA("&aPlease use the QR code given to setup two-factor authentication"),
        INVENTORY_FULL_USE_CLICKABLE_MESSAGE("&cYour inventory is full! Please use the clickable message above to get your QR code!"),
        SETUP_RECOMMENDATION("&6This server supports two-factor authentication and is highly recommended"),
        GET_STARTED("&6Get started by running \"/2fa enable\""),
        DIDNT_SETUP_YET("&cYou didn't enable 2FA yet. Get started by using \"/2fa enable\"!"),
        TWOFA_IS_ENABLED("&cTwo-factor authentication is enabled on this account"),
        PLEASE_AUTHENTICATE("&cPlease authenticate using /2fa <code>"),
        CLICK_TO_OPEN_QR("&eClick here to open the QR code"),
        RESET_FOR("&a%name%'s 2FA has been reset"),
        PLAYER_NOT_FOUND("&c%name% could not be found"),
        PLAYER_NOT_SETUP("&c%name% is not setup with 2FA"),
        RELOADED_CONFIG("&aConfig was reloaded"),
        FAILED_AUTHENTICATION_ALERT("&c%name% failed to authenticate %times% times"),
        SOMETHING_WENT_WRONG("&cSomething went wrong. Please contact a Staff Member!"),
        HELP_HEADER("&7----- &b2FA &7-----"),
        HELP_COMMAND("&7\u2022 &b%command% &e%aliases%&7: %description%"),
        HELP_FOOTER("&7----- -------- -----"),
        DESCRIPTION_OF_DISABLE_COMMAND("Disables 2FA"),
        DESCRIPTION_OF_DISABLE_OTHERS_COMMAND("Disable 2FA for other users"),
        DESCRIPTION_OF_ENABLE_COMMAND("Enables 2FA"),
        DESCRIPTION_OF_HELP_COMMAND("Shows the Help Menu of the 2FA Plugin"),
        DESCRIPTION_OF_RELOAD_COMMAND("Reloads the Config"),
        DESCRIPTION_OF_PRINT_INFO_COMMAND("Prints useful info about the plugin");

        private String message;

        TwoFAMessages(@NotNull final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }
    }
}