package com.lielamar.auth.database.mysql;

import com.lielamar.auth.Main;
import com.lielamar.auth.database.AuthenticationDatabase;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.UUID;

public class AuthMySQL implements AuthenticationDatabase {

    private Main main;

    private Connection connection;
    private String host, database, username, password;
    private int port;

    public void AuthMySQL(Main main) {
        this.main = main;
    }


    /**
     * Sets up the database from the Config.yml file
     *
     * @return   Whether or not connection was successful
     */
    public boolean setupDatabase() {
        if(this.main.getConfig() == null)
            this.main.saveConfig();

        if(!this.main.getConfig().getBoolean("MySQL.enabled")) return false;

        ConfigurationSection mysqlSection = this.main.getConfig().getConfigurationSection("MySQL");
        this.host = mysqlSection.getString("credentials.host");
        this.database = mysqlSection.getString("credentials.database");
        this.username = mysqlSection.getString("credentials.username");
        this.password = mysqlSection.getString("credentials.password");
        this.port = mysqlSection.getInt("credentials.port");

        try {
            openConnection();
            return true;
        } catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            this.connection = null;
            return false;
        }
    }

    /**
     * Opens a MySQL connection
     *
     * @throws SQLException             Throws an exception if couldn't connect to the sql database
     * @throws ClassNotFoundException   Throws an exception if the Driver class wasn't found
     */
    private void openConnection() throws SQLException, ClassNotFoundException {
        if(isValidConnection()) return;

        synchronized(this) {
            if(isValidConnection()) return;

            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s", this.host, this.port, this.database), this.username, this.password);

            createTables();
        }
    }

    /**
     * Creates the required tables on the database
     *
     * @throws SQLException   Throws an exception if couldn't connect to the sql database
     */
    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Auth(uuid varchar(64), secret_key varchar(64));";
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        stmt.executeUpdate();
    }

    /**
     * Checks if the connection is valid (not null & open)
     *
     * @return                Whether or not the connection is valid
     * @throws SQLException   Throws an exception if the connection is not available
     */
    public boolean isValidConnection() throws SQLException {
        return this.connection != null && !this.connection.isClosed();
    }


    @Override
    public String setSecretKey(UUID uuid, String secretKey) {
        try {
            if(!isValidConnection()) return null;

            PreparedStatement statement;
            if(!hasSecretKey(uuid)) {
                statement = this.connection.prepareStatement("INSERT INTO Auth(uuid, secret_key) VALUES (?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, secretKey);
            } else {
                statement = this.connection.prepareStatement("UPDATE Auth SET secret_key = ? WHERE uuid = ?;");
                statement.setString(1, secretKey);
                statement.setString(2, uuid.toString());
            }
            statement.executeUpdate();
            return secretKey;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSecretKey(UUID uuid) {
        try {
            if(!isValidConnection()) return null;

            PreparedStatement statement = this.connection.prepareStatement("SELECT secret_key FROM Auth WHERE uuid = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next())
                return result.getString("secret_key");
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasSecretKey(UUID uuid) {
        return getSecretKey(uuid) != null;
    }

    @Override
    public void removeSecretKey(UUID uuid) {
        try {
            if(!isValidConnection()) return;

            PreparedStatement statement = this.connection.prepareStatement("DELETE FROM Auth WHERE uuid = ?;");
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
