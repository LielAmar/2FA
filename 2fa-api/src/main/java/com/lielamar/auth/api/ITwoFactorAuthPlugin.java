package com.lielamar.auth.api;

public interface ITwoFactorAuthPlugin {

    /**
     * Runs a reload action on the plugin to update values that may
     * have changes in the config.
     * <p>
     * Some features may require a restart to apply the changes to them such as
     * the MongoManager, which we do not want to reload its connection due to it
     * causing corruptions and errors.
     *
     * @since 2.0
     */
    void reloadPlugin();

}
