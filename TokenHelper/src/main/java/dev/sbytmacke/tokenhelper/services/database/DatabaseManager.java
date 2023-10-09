package dev.sbytmacke.tokenhelper.services.database;

import com.mongodb.client.MongoDatabase;

public interface DatabaseManager {

    MongoDatabase connectDatabase();

    void closeDatabase();

    MongoDatabase getDatabase();

    String getConnectionString();

    String getDatabaseName();
}
