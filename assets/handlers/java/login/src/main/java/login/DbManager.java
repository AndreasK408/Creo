package login;

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
    private static final String DB_NAME = "login_db";
    private static final String COLLECTION_NAME = "login_collection";

    private static final MongoClient mongoClient;
    private static final MongoDatabase database;

    static {
        try {
            String host = System.getenv(ENV_MONGO_HOST);
            String port = System.getenv(ENV_MONGO_PORT);
            String user = System.getenv(ENV_MONGO_USER);
            String password = System.getenv(ENV_MONGO_PASSWORD);
            if (host == null) throw new IllegalStateException(ENV_MONGO_HOST + " not set.");
            if (user == null) throw new IllegalStateException(ENV_MONGO_USER + " not set.");
            if (password == null) throw new IllegalStateException(ENV_MONGO_PASSWORD + " not set.");
            if (port == null || port.trim().isEmpty()) port = "27017";

            String connectionString = String.format("mongodb://%s:%s@%s:%s/?authSource=admin",
                    user, password, host, port);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DB_NAME);
            database.runCommand(new Document("ping", 1)); // Verbindungstest
            System.out.println("Successfully connected to MongoDB for login handler.");

            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            collection.createIndex(new Document("username", 1));
            collection.createIndex(new Document("email", 1));
            System.out.println("Indexes for username and email ensured on " + COLLECTION_NAME);
        } catch (Exception e) {
            System.err.println("FATAL: Failed to initialize MongoDB connection for login: " + e.getMessage());
            throw new RuntimeException("MongoDB initialization failed for login", e);
        }
    }

    public static MongoCollection<Document> getLoginCollection() {
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized for login.");
        }
        return database.getCollection(COLLECTION_NAME);
    }
}