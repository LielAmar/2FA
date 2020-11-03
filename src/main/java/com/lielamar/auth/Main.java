package com.lielamar.auth;

import com.lielamar.auth.authentication.AuthenticationManager;
import com.lielamar.auth.commands.AuthCommand;
import com.lielamar.auth.database.AuthenticationDBManager;
import com.lielamar.auth.listeners.OnPlayerJoin;
import com.lielamar.auth.utils.AuthenticationVariables;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private AuthenticationVariables authVars;
    private AuthenticationManager authManager;
    private AuthenticationDBManager database;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.setupAuth();

        this.registerListeners();
        this.registerCommands();
    }

    public void setupAuth() {
        this.authVars = new AuthenticationVariables(this);
        this.authManager = new AuthenticationManager(this);
        this.database = new AuthenticationDBManager(this);
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnPlayerJoin(this), this);
    }
    public void registerCommands() {
        getCommand("2fa").setExecutor(new AuthCommand(this));
    }

    public AuthenticationVariables getAuthVars() { return this.authVars; }
    public AuthenticationManager getAuthManager() { return this.authManager; }
    public AuthenticationDBManager getAuthDatabaseManager() { return this.database; }
}
