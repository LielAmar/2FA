package com.lielamar.auth.shared.storage.sql;

import com.lielamar.auth.shared.storage.StorageHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public class SQLStorage extends StorageHandler {

    protected HikariDataSource hikari;

    private final String driver;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    private final int maximumPoolSize;
    private final int minimumIdle;
    private final int maximumLifetime;
    private final int keepAliveTime;
    private final int connectionTimeout;

    private final String fullPlayersTableName;

    private boolean loaded = false;

    public SQLStorage(String driver, String host, String database, String username, String password, int port,
            String tablePrefix, int maximumPoolSize, int minimumIdle, int maximumLifetime, int keepAliveTime, int connectionTimeout) {
        this.driver = driver;

        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port != -1 ? port : 3306;

        this.maximumPoolSize = maximumPoolSize;
        this.minimumIdle = minimumIdle;
        this.maximumLifetime = maximumLifetime;
        this.keepAliveTime = keepAliveTime;
        this.connectionTimeout = connectionTimeout;

        this.fullPlayersTableName = tablePrefix + "players";

        try {
            loaded = true;
            this.setupHikari();
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("Something went wrong when setting up HikariCP. If something does not work"
                    + " correctly, please report it to the 2FA team");

            loaded = false;
        }
    }

    /**
     * Sets up Hikari
     */
    private void setupHikari() {
        hikari = new HikariDataSource();

        try {
            loaded = true;
            
            hikari.setMaximumPoolSize(this.maximumPoolSize);
            hikari.setMinimumIdle(this.minimumIdle);
            hikari.setMaxLifetime(this.maximumLifetime);
            hikari.setKeepaliveTime(this.keepAliveTime);
            hikari.setConnectionTimeout(this.connectionTimeout);
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("Something went wrong when setting up HikariCP. If something does not work"
                    + " correctly, please report it to the 2FA team");

            loaded = false;
        }

        hikari.setDataSourceClassName(this.driver);

        Properties properties = new Properties();
        properties.setProperty("serverName", host);
        properties.setProperty("portNumber", port + "");
        properties.setProperty("databaseName", database);
        properties.setProperty("user", username);
        if (!password.isEmpty()) {
            properties.setProperty("password", password);
        }
        hikari.setDataSourceProperties(properties);

        this.createTables();
    }

    /**
     * Creates the required tables on the database
     */
    protected void createTables() {
        Connection connection = null;

        try {
            loaded = true;
            
            connection = hikari.getConnection();

            String sql = "CREATE TABLE IF NOT EXISTS " + this.fullPlayersTableName + " (`uuid` varchar(64), `key` varchar(64), `ip` varchar(256), `enable_date` long);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();

            // Validating the SQL has the required columns. Ignoring the error because it can only throw an error when the SQL user doesn't have access to information_schema.
            try {
                sql = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + this.fullPlayersTableName + "' and column_name = 'enable_date'";
                stmt = connection.prepareStatement(sql);
                if (!stmt.executeQuery().next()) {
                    sql = "ALTER TABLE " + this.fullPlayersTableName + " ADD `enable_date` long DEFAULT -1;";
                    stmt = connection.prepareStatement(sql);
                    stmt.executeUpdate();
                }
            } catch (Exception ignored) {
                Bukkit.getServer().getLogger().severe("The plugin could not add the 'enable_date' column to your SQL database in table: " + this.fullPlayersTableName + "."
                        + "Please give your SQL user permissions to information_schema or add the column manually, otherwise the plugin won't work properly!");
                
                loaded = false;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                    loaded = false;
                }
            }
        }
    }

    @Override
    public String setKey(UUID uuid, String key) {
        Connection connection = null;

        try {
            loaded = true;
            connection = hikari.getConnection();
            if (connection.isClosed()) {
                return null;
            }

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT `key` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                statement = connection.prepareStatement("UPDATE " + this.fullPlayersTableName + " SET `key` = ? WHERE `uuid` = ?;");
                statement.setString(1, key);
                statement.setString(2, uuid.toString());
            } else {
                statement = connection.prepareStatement("INSERT INTO " + this.fullPlayersTableName + "(`uuid`, `key`, `ip`, `enable_date`) VALUES (?,?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, key);
                statement.setString(3, "");
                statement.setLong(4, -1);
            }

            statement.executeUpdate();
            return key;
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public String getKey(UUID uuid) {
        Connection connection = null;

        try {
            loaded = true;
            connection = hikari.getConnection();
            if (connection.isClosed()) {
                return null;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT `key` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String key = result.getString("key");

                return key.equalsIgnoreCase("") ? null : key;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
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
    public String setIP(UUID uuid, String ip) {
        Connection connection = null;

        try {
            loaded = true;
            connection = hikari.getConnection();
            if (connection.isClosed()) {
                return null;
            }

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                statement = connection.prepareStatement("UPDATE " + this.fullPlayersTableName + " SET `ip` = ? WHERE `uuid` = ?;");
                statement.setString(1, ip);
                statement.setString(2, uuid.toString());
            } else {
                statement = connection.prepareStatement("INSERT INTO " + this.fullPlayersTableName + "(`uuid`, `key`, `ip`, `enable_date`) VALUES (?,?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, "");
                statement.setString(3, ip);
                statement.setLong(4, -1);
            }

            statement.executeUpdate();
            return ip;
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public String getIP(UUID uuid) {
        Connection connection = null;

        try {
            loaded = true;
            connection = hikari.getConnection();
            if (connection.isClosed()) {
                return null;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT `ip` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String IP = result.getString("ip");
                return IP.equalsIgnoreCase("") ? null : IP;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public boolean hasIP(UUID uuid) {
        return this.getIP(uuid) != null;
    }

    @Override
    public long setEnableDate(UUID uuid, long enableDate) {
        Connection connection = null;

        try {
            loaded = true;
            connection = hikari.getConnection();
            if (connection.isClosed()) {
                return -1;
            }

            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                statement = connection.prepareStatement("UPDATE " + this.fullPlayersTableName + " SET `enable_date` = ? WHERE `uuid` = ?;");
                statement.setLong(1, enableDate);
                statement.setString(2, uuid.toString());
            } else {
                statement = connection.prepareStatement("INSERT INTO " + this.fullPlayersTableName + "(`uuid`, `key`, `ip`, `enable_date) VALUES (?,?,?,?);");
                statement.setString(1, uuid.toString());
                statement.setString(2, "");
                statement.setString(3, "");
                statement.setLong(4, enableDate);
            }

            statement.executeUpdate();
            return enableDate;
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return -1;
    }

    @Override
    public long getEnableDate(UUID uuid) {
        Connection connection = null;

        try {
            loaded = true;
            connection = hikari.getConnection();
            if (connection.isClosed()) {
                return -1;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT `enable_date` FROM " + this.fullPlayersTableName + " WHERE `uuid` = ?;");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getLong("enableDate");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            loaded = false;
        } finally {
            loaded = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return -1;
    }

    @Override
    public boolean hasEnableDate(UUID uuid) {
        return getEnableDate(uuid) != -1;
    }

    @Override
    public void unload() {
        if (!hikari.isClosed()) {
            hikari.close();
        }
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
