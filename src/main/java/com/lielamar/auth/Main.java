package com.lielamar.auth;

import com.lielamar.auth.authentication.AuthenticationManager;
import com.lielamar.auth.database.AuthenticationDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private AuthenticationManager authManager;
    private AuthenticationDatabase database;

    @Override
    public void onEnable() {
        authManager = new AuthenticationManager(this);
    }

    public AuthenticationManager getAuthManager() { return this.authManager; }
    public AuthenticationDatabase getAuthDatabase() { return this.database; }
}
