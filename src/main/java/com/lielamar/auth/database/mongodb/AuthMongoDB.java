package com.lielamar.auth.database.mongodb;

import com.lielamar.auth.Main;
import com.lielamar.auth.database.AuthenticationDatabase;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class AuthMongoDB implements AuthenticationDatabase{

    private final Main main;

    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;

    private String uri;
    private String host, database, username, password;
    private boolean requiredAuth;
    private int port;

    public AuthMongoDB(Main main) {
        this.main = main;
    }


    /**
     * Sets up the database from the Config.yml file
     *
     * @return   Whether or not connection was successful
     */
    public boolean setup() {
        if(this.main.getConfig() == null)
            this.main.saveConfig();

        if(!this.main.getConfig().getBoolean("MongoDB.enabled")) return false;

        ConfigurationSection mysqlSection = this.main.getConfig().getConfigurationSection("MongoDB");
        this.uri = mysqlSection.getString("credentials.uri");
        this.host = mysqlSection.getString("credentials.database");
        this.database = mysqlSection.getString("credentials.database");
        this.port = mysqlSection.getInt("credentials.port");
        this.username = mysqlSection.getString("credentials.auth.username");
        this.password = mysqlSection.getString("credentials.auth.password");
        this.requiredAuth = mysqlSection.getBoolean("credentials.auth.required");

        openConnection();
        return true;
    }

    /**
     * Opens a MongoDB connection
     */
    private void openConnection() {
        if(this.mongoClient != null)
            throw new IllegalStateException("A MongoDB instance already exists for the following database: " + this.database);

        if(!this.uri.equalsIgnoreCase("null")) {
            MongoClientURI uri = new MongoClientURI(this.uri);
            this.mongoClient = new MongoClient(uri);
        } else {
            if(this.requiredAuth) {
                MongoCredential credential = MongoCredential.createCredential(this.username, this.database, this.password.toCharArray());
                this.mongoClient = new MongoClient(new ServerAddress(this.host, this.port), credential, MongoClientOptions.builder().build());
            } else {
                this.mongoClient = new MongoClient(new ServerAddress(this.host, this.port));
            }
        }

        MongoDatabase mongoDatabase = mongoClient.getDatabase(this.database);
        this.mongoCollection = mongoDatabase.getCollection("players");
    }


    @Override
    public String setSecretKey(UUID uuid, String secretKey) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if(playerDocument == null) {
            Document insertDocument = new Document("uuid", uuid.toString());
            insertDocument.append("secret_key", secretKey);
            insertDocument.append("last_ip", null);
            this.mongoCollection.insertOne(insertDocument);
        } else {
            playerDocument.put("secret_key", secretKey);
            this.mongoCollection.updateOne(query, playerDocument);
        }

        AuthenticationDatabase.cachedKeys.put(uuid, secretKey);
        return secretKey;
    }

    @Override
    public String getSecretKey(UUID uuid) {
        if(AuthenticationDatabase.cachedKeys.containsKey(uuid))
            return AuthenticationDatabase.cachedKeys.get(uuid);

        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if(playerDocument != null) {
            String secretKey = playerDocument.getString("secret_key");
            AuthenticationDatabase.cachedKeys.put(uuid, secretKey);
            return secretKey;
        }
        return null;
    }

    @Override
    public boolean hasSecretKey(UUID uuid) {
        return this.getSecretKey(uuid) != null;
    }

    @Override
    public void removeSecretKey(UUID uuid) {
        this.setSecretKey(uuid, null);
    }


    @Override
    public String setLastIP(UUID uuid, String lastIP) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if(playerDocument == null) {
            Document insertDocument = new Document("uuid", uuid.toString());
            insertDocument.append("secret_key", null);
            insertDocument.append("last_ip", lastIP);
            this.mongoCollection.insertOne(insertDocument);
        } else {
            playerDocument.put("last_ip", lastIP);
            this.mongoCollection.updateOne(query, playerDocument);
        }

        return lastIP;
    }

    @Override
    public String getLastIP(UUID uuid) {
        if(AuthenticationDatabase.cachedKeys.containsKey(uuid))
            return AuthenticationDatabase.cachedKeys.get(uuid);

        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if(playerDocument != null) {
            String lastIP = playerDocument.getString("last_ip");
            return lastIP;
        }
        return null;
    }

    @Override
    public boolean hasLastIP(UUID uuid) {
        return this.getLastIP(uuid) != null;
    }

    @Override
    public void removeLastIP(UUID uuid) {
        this.setLastIP(uuid, null);
    }
}
