package com.lielamar.auth;

import com.lielamar.auth.authentication.AuthenticationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private AuthenticationManager authManager;

    @Override
    public void onEnable() {
        authManager = new AuthenticationManager(this);
    }
}
