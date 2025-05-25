package seedinvoice;

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

    private static final String DB_NAME = "invoice_db";
    private static final String COLLECTION_NAME = "invoice_collection";

    private static MongoClient mongoClientInstance = null;
    private static MongoDatabase databaseInstance = null;

    private static void initializeConnection() {
        if (mongoClientInstance == null) {
            synchronized (DbManager.class) {
                if (mongoClientInstance == null) {
                    try {
                        String host = System.getenv(ENV_MONGO_HOST);
                        String portEnv = System.getenv(ENV_MONGO_PORT);
                        String user = System.getenv(ENV_MONGO_USER);
                        String password = System.getenv(ENV_MONGO_PASSWORD);

                        if (host == null || host.trim().isEmpty()) {
                            throw new IllegalStateException(ENV_MONGO_HOST + " environment variable not set.");
                        }
                        int port = (portEnv == null || portEnv.trim().isEmpty()) ? 27017 : Integer.parseInt(portEnv);
                        if (user == null || user.trim().isEmpty()) {
                            throw new IllegalStateException(ENV_MONGO_USER + " environment variable not set.");
                        }
                        if (password == null) {
                            throw new IllegalStateException(ENV_MONGO_PASSWORD + " environment variable not set.");
                        }

                        String connectionString = String.format("mongodb://%s:%s@%s:%d/?authSource=admin",
                                user, password, host, port);
                        System.out.println("Seed-Invoice: Connecting to MongoDB with: " + host + ":" + port);

                        MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(new ConnectionString(connectionString))
                                .uuidRepresentation(UuidRepresentation.STANDARD)
                                .build();

                        mongoClientInstance = MongoClients.create(settings);
                        databaseInstance = mongoClientInstance.getDatabase(DB_NAME);
                        System.out.println("Seed-Invoice: Successfully connected to MongoDB database '" + DB_NAME + "'.");
                        databaseInstance.runCommand(new Document("ping", 1));
                        System.out.println("Seed-Invoice: Ping successful.");

                    } catch (Exception e) {
                        System.err.println("Seed-Invoice: FATAL - Failed to initialize MongoDB connection: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("MongoDB initialization failed for seed-invoice", e);
                    }
                }
            }
        }
    }

    public static MongoCollection<Document> getInvoiceCollection() {
        initializeConnection();
        if (databaseInstance == null) {
            throw new IllegalStateException("Database connection is not initialized for seed-invoice.");
        }
        return databaseInstance.getCollection(COLLECTION_NAME);
    }

    public static void closeConnection() {
        if (mongoClientInstance != null) {
            System.out.println("Seed-Invoice: Closing MongoDB connection...");
            mongoClientInstance.close();
            mongoClientInstance = null;
            databaseInstance = null;
            System.out.println("Seed-Invoice: MongoDB connection closed.");
        }
    }
}
     