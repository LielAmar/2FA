package com.lielamar.auth.shared.storage.mysql;

import com.lielamar.auth.shared.storage.StorageHandler;
import com.lielamar.auth.shared.storage.StorageType;

import java.sql.*;
import java.util.UUID;

public class MySQLStorage extends StorageHandler {

    private final StorageType storageType = StorageType.MYSQL;

    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public MySQLStorage(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;

        try { openConnection(); }
        catch(SQLException | ClassNotFoundException e) { e.printStackTrace(); }
    }


    /**
     * Opens a MySQL connection
     *
     * @throws SQLException             Throws an exception if couldn't connect to the sql database
     * @throws ClassNotFoundException   Throws an exception if the Driver class wasn't found
     */
    private void openConnection() throws SQLException, ClassNotFoundException {
        if(isValidConnection())
            throw new IllegalStateException("A MySQL instance already exists for the following database: " + this.database);

        synchronized(this) {
            if(isValidConnection()) throw new IllegalStateException("A MySQL instance already exists for the following database: " + this.database);

            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s", this.host, this.port, this.database), this.username, this.password);
            this.createTables();
        }
    }

    /**
     * Creates the required tables on the database
     *
     * @throws SQLException   Throws an exception if couldn't connect to the sql database
     */
    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Auth(`uuid` varchar(64), `key` varchar(64), `ip` varchar(64));";
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
    public StorageType getStorageType() {
        return storageType;
    }


    @Override
    public String setKey(UUID uuid, String Key) {
        try {
            if(!isValidConnection()) return null;

            PreparedStatement statement;
            statement = this.connection.prepareStatement("SELECT `key` FROM Auth WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                statement = this.connection.prepareStatement("UPDATE Auth SET `key` = ? WHERE `uuid` = ?;");
                statement.setString(1, Key);
                statement.setString(2, uuid.toString());
            } else {
                statement = this.connection.prepareStatement("INSERT INTO Auth(`uuid`, `key`, `ip`) VALUES (?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, Key);
                statement.setString(3, "");
            }

            statement.executeUpdate();
            return Key;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getKey(UUID uuid) {
        try {
            if(!isValidConnection()) return null;

            PreparedStatement statement = this.connection.prepareStatement("SELECT `key` FROM Auth WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String Key = result.getString("key");
                return Key.equalsIgnoreCase("") ? null : Key;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasKey(UUID uuid) {
        return this.getKey(uuid) != null;
    }

    @Override
    public void removeKey(UUID uuid) {
        this.setKey(uuid, "");
    }


    @Override
    public String setIP(UUID uuid, String IP) {
        try {
            if(!isValidConnection()) return null;

            PreparedStatement statement;
            statement = this.connection.prepareStatement("SELECT * FROM Auth WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                statement = this.connection.prepareStatement("UPDATE Auth SET `ip` = ? WHERE `uuid` = ?;");
                statement.setString(1, IP);
                statement.setString(2, uuid.toString());
            } else {
                statement = this.connection.prepareStatement("INSERT INTO Auth(`uuid`, `key`, `ip`) VALUES (?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, "");
                statement.setString(3, IP);
            }

            statement.executeUpdate();
            return IP;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getIP(UUID uuid) {
        try {
            if(!isValidConnection()) return null;

            PreparedStatement statement = this.connection.prepareStatement("SELECT `ip` FROM Auth WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String IP = result.getString("ip");
                return IP.equalsIgnoreCase("") ? null : IP;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasIP(UUID uuid) {
        return this.getIP(uuid) != null;
    }
}