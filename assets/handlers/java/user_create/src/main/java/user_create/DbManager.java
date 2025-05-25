package user_create;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.UuidRepresentation;

public class DbManager {
    private static final String ENV_MONGO_HOST = "DB_MONGO_HOST";
    private static final String ENV_MONGO_PORT = "DB_MONGO_PORT";
    private static final String ENV_MONGO_USER = "DB_MONGO_USER";
    private static final String ENV_MONGO_PASSWORD = "DB_MONGO_PASSWORD";
    private static final String DB_NAME = "user_db";
    private static final String COLLECTION_NAME = "user_collection";
    private static final MongoClient mongoClient;
    private static final MongoDatabase database;

    // Statische initialisierung der datenbank Verbindung
    static {
        try {
            String host = System.getenv(ENV_MONGO_HOST);
            String port = System.getenv(ENV_MONGO_PORT);
            String user = System.getenv(ENV_MONGO_USER);
            String password = System.getenv(ENV_MONGO_PASSWORD);
            if (host == null) throw new IllegalStateException(ENV_MONGO_HOST + " not set.");
            if (user == null) throw new IllegalStateException(ENV_MONGO_USER + " not set.");
            if (password == null) throw new IllegalStateException(ENV_MONGO_PASSWORD + " not set.");
            if (port == null || port.trim().isEmpty()) port = "27017"; // Default Port
            String connectionString = String.format("mongodb://%s:%s@%s:%s/?authSource=admin",
                    user, password, host, port);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DB_NAME);

            // einfacher verbindungstest (optional)
            database.runCommand(new Document("ping", 1));
            System.out.println("Successfully connected to MongoDB.");
        } catch (Exception e) {
            System.err.println("FATAL: Failed to initialize MongoDB connection: " + e.getMessage());
            throw new RuntimeException("MongoDB initialization failed", e);
        }
    }

    public static MongoCollection<Document> getUserCollection() {
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }
        return database.getCollection(COLLECTION_NAME);
    }
}