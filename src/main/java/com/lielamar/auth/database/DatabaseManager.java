package com.lielamar.auth.database;

import com.lielamar.auth.Main;
import com.lielamar.auth.database.mysql.AuthMySQL;

public class DatabaseManager {

    private final Main main;

    private AuthenticationDatabase database;

    public DatabaseManager(Main main) {
        this.main = main;

        setupDatabase();
    }

    public AuthenticationDatabase setupDatabase() {
        if(main.getConfig().contains("MySQL.enabled") && main.getConfig().getBoolean("MySQL.enabled"))
            this.database = new AuthMySQL(this.main);
//        else if(main.getConfig().contains("MongoDB.enabled") && main.getConfig().getBoolean("MongoDB.enabled"))
//            this.database = new AuthMongoDB(this.main);
//        else
//            this.database = new AuthFlat(this.main);

        return this.database;
    }

    public AuthenticationDatabase getDatabase() {
        return this.database;
    }
    public AuthenticationDatabase reload() {
        return this.setupDatabase();
    }
}
