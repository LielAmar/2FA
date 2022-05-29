package com.lielamar.auth.shared.storage.mongodb;

import com.lielamar.auth.shared.storage.StorageHandler;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.UUID;

public final class MongoDBStorage extends StorageHandler {

    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;

    private final String host;
    private final String database;
    private final int port;
    private final String username;
    private final String password;

    private final String uri;

    private final String fullPlayersCollectionName;

    public MongoDBStorage(String host, String database, String username, String password, int port,
            String collectionPrefix, String uri) {
        this.host = host;
        this.database = database;
        this.port = port;
        this.username = username;
        this.password = password;

        this.uri = uri;

        this.fullPlayersCollectionName = collectionPrefix + "players";

        openConnection();
    }

    /**
     * Opens a MongoDB connection
     */
    public void openConnection() {
        if (this.mongoClient != null) {
            throw new IllegalStateException("A MongoDB instance already exists for the following database: " + this.database);
        }

        if (this.uri.length() > 0) {
            MongoClientURI uri = new MongoClientURI(this.uri);
            this.mongoClient = new MongoClient(uri);
        } else {
            if (this.username.length() > 0 && this.password.length() > 0) {
                MongoCredential credential = MongoCredential.createCredential(this.username, this.database, this.password.toCharArray());
                this.mongoClient = new MongoClient(new ServerAddress(this.host, this.port), credential, MongoClientOptions.builder().build());
            } else {
                this.mongoClient = new MongoClient(new ServerAddress(this.host, this.port));
            }
        }

        MongoDatabase mongoDatabase = mongoClient.getDatabase(this.database);
        this.mongoCollection = mongoDatabase.getCollection(fullPlayersCollectionName);
    }

    @Override
    public String setKey(UUID uuid, String key) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if (playerDocument == null) {
            Document insertDocument = new Document("uuid", uuid.toString());
            insertDocument.append("key", key);
            insertDocument.append("ip", null);
            insertDocument.append("enable_date", -1);
            this.mongoCollection.insertOne(insertDocument);
        } else {
            playerDocument.put("key", key);
            this.mongoCollection.updateOne(query, new Document("$set", playerDocument));
        }

        return key;
    }

    @Override
    public String getKey(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if (playerDocument != null) {
            return playerDocument.getString("key");
        }
        return null;
    }

    @Override
    public boolean hasKey(UUID uuid) {
        return getKey(uuid) != null;
    }

    @Override
    public void removeKey(UUID uuid) {
        setKey(uuid, null);
    }

    @Override
    public String setIP(UUID uuid, String ip) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if (playerDocument == null) {
            Document insertDocument = new Document("uuid", uuid.toString());
            insertDocument.append("key", null);
            insertDocument.append("ip", ip);
            insertDocument.append("enable_date", -1);
            this.mongoCollection.insertOne(insertDocument);
        } else {
            playerDocument.put("ip", ip);
            this.mongoCollection.updateOne(query, new Document("$set", playerDocument));
        }

        return ip;
    }

    @Override
    public String getIP(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if (playerDocument != null) {
            return playerDocument.getString("ip");
        }
        return null;
    }

    @Override
    public boolean hasIP(UUID uuid) {
        return getIP(uuid) != null;
    }

    @Override
    public long setEnableDate(UUID uuid, long enableDate) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if (playerDocument == null) {
            Document insertDocument = new Document("uuid", uuid.toString());
            insertDocument.append("key", null);
            insertDocument.append("ip", null);
            insertDocument.append("enable_date", enableDate);
            this.mongoCollection.insertOne(insertDocument);
        } else {
            playerDocument.put("enable_date", enableDate);
            this.mongoCollection.updateOne(query, new Document("$set", playerDocument));
        }

        return enableDate;
    }

    @Override
    public long getEnableDate(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document playerDocument = this.mongoCollection.find(query).first();

        if (playerDocument != null) {
            return playerDocument.getLong("enable_date");
        }
        return -1;
    }

    @Override
    public boolean hasEnableDate(UUID uuid) {
        return getEnableDate(uuid) != -1;
    }

    @Override
    public void unload() {
        mongoClient.close();
    }
}
