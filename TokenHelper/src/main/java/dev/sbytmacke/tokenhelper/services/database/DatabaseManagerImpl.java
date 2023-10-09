package dev.sbytmacke.tokenhelper.services.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseManagerImpl implements DatabaseManager {

    // Con el ConfigApp
    private final String connectionString = "mongodb://localhost:27017";
    private final String databaseName = "token_helper";
    private MongoDatabase database = null;
    private MongoClient mongoClient = null;

    @Override
    public MongoDatabase connectDatabase() {

        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(databaseName);

        return database;
    }

    @Override
    public void closeDatabase() {
        mongoClient.close();
    }

    @Override
    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }
}
