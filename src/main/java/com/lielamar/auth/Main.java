package com.lielamar.auth;

import com.lielamar.auth.authentication.AuthenticationManager;
import com.lielamar.auth.database.DatabaseModule;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private AuthenticationManager authManager;
    private DatabaseModule database;

    @Override
    public void onEnable() {
        authManager = new AuthenticationManager(this);
    }

    public AuthenticationManager getAuthManager() { return this.authManager; }
    public DatabaseModule getAuthDatabase() { return this.database; }
}
