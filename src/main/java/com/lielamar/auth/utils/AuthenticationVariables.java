package com.lielamar.auth.utils;

import com.lielamar.auth.Main;

public class AuthenticationVariables {

    private final boolean requireOnIPChange;
    private final boolean requireOnEveryLogin;

    public AuthenticationVariables(Main main) {
        if(main.getConfig() == null)
            main.saveDefaultConfig();

        requireOnIPChange = main.getConfig().getBoolean("require.ipchanges");
        requireOnEveryLogin = main.getConfig().getBoolean("require.everylogin");
    }

    /**
     * Is it required to ask for the 2FA code when IP changes
     *
     * @return   Whether or not it is required
     */
    public boolean isRequiredOnIPChange() {
        return this.requireOnIPChange;
    }

    /**
     * Is it required to ask for the 2FA code everytime they join
     *
     * @return   Whether or not it is required
     */
    public boolean isRequiredOnEveryLogin() {
        return this.requireOnEveryLogin;
    }
}