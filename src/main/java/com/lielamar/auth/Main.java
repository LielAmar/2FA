package com.lielamar.auth;

import com.lielamar.auth.authentication.AuthenticationManager;
import com.lielamar.auth.commands.AuthCommand;
import com.lielamar.auth.database.DatabaseManager;
import com.lielamar.auth.listeners.OnPlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private AuthenticationManager authManager;
    private DatabaseManager database;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.authManager = new AuthenticationManager(this);
        this.database = new DatabaseManager(this);

        this.registerListeners();
        this.registerCommands();
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new OnPlayerJoin(this), this);
    }
    public void registerCommands() {
        getCommand("2fa").setExecutor(new AuthCommand(this));
    }

    public AuthenticationManager getAuthManager() { return this.authManager; }
    public DatabaseManager getAuthDatabaseManager() { return this.database; }
}
