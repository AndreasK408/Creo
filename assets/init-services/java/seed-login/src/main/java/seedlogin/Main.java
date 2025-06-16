package seedlogin;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int DEFAULT_SEED_COUNT = 1000;
    private static final int BATCH_SIZE = 5000;

    public static void main(String[] args) {
        System.out.println("Seed-Login (Java) starting...");
        long startTime = System.currentTimeMillis();

        String seedCountEnvRaw = System.getenv("MG_SEED_COUNT");
        System.out.println("Seed-Login: Raw MG_SEED_COUNT from env: '" + seedCountEnvRaw + "'");

        int seedCount;
        try {
            if (seedCountEnvRaw != null && !seedCountEnvRaw.trim().isEmpty()) {
                seedCount = Integer.parseInt(seedCountEnvRaw.trim());
            } else {
                System.out.println("INFO: MG_SEED_COUNT environment variable not set or empty, using default: " + DEFAULT_SEED_COUNT);
                seedCount = DEFAULT_SEED_COUNT;
            }

            if (seedCount <= 0) {
                System.out.println("INFO: Seed count is " + seedCount + ", no records will be inserted.");
                System.out.println("Seed-Login (Java) finished in " + (System.currentTimeMillis() - startTime) + "ms.");
                DbManager.closeConnection();
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Could not parse MG_SEED_COUNT environment variable: '" + seedCountEnvRaw + "'. " + e.getMessage() + ". Using default: " + DEFAULT_SEED_COUNT);
            seedCount = DEFAULT_SEED_COUNT;
        }

        System.out.println("Target seed count: " + seedCount);
        System.out.println("Batch size: " + BATCH_SIZE);

        MongoCollection<Document> collection = null;
        try {
            collection = DbManager.getLoginCollection();
            System.out.println("Seed-Login: Obtained collection '" + DbManager.COLLECTION_NAME + "' in database '" + DbManager.DB_NAME + "'.");

            try {
                System.out.println("Seed-Login: Attempting to drop collection '" + DbManager.COLLECTION_NAME + "'...");
                collection.drop();
                System.out.println("Seed-Login: Collection '" + DbManager.COLLECTION_NAME + "' dropped successfully (or did not exist).");
            } catch (Exception e) {
                System.err.println("Seed-Login: Info/Warning - Could not drop collection '" + DbManager.COLLECTION_NAME + "'. It might not exist or DB user lacks permissions. Error: " + e.getMessage());
            }

            try {
                System.out.println("Seed-Login: Ensuring indexes on 'username' and 'email' in '" + DbManager.COLLECTION_NAME + "'...");
                collection.createIndex(Indexes.ascending("username"));
                collection.createIndex(Indexes.ascending("email"));
                System.out.println("Seed-Login: Indexes ensured.");
            } catch (Exception e) {
                System.err.println("Seed-Login: Warning - Could not create indexes: " + e.getMessage());
            }

            List<Document> userBatch = new ArrayList<>(BATCH_SIZE);
            int totalInserted = 0;
            long currentBatchStartTime = System.currentTimeMillis();

            for (int i = 1; i <= seedCount; i++) {
                User userPojo = new User(i);
                Document userDoc = userPojo.toDocument();

                System.out.println("Seed-Login: Preparing to insert user _id: " + userDoc.getLong("_id") + ", username: " + userDoc.getString("username"));

                userBatch.add(userDoc);

                if (userBatch.size() >= BATCH_SIZE || i == seedCount) {
                    if (!userBatch.isEmpty()) {
                        System.out.println("Inserting batch of " + userBatch.size() + " users into " + DbManager.COLLECTION_NAME + "... (up to id " + i + ")");
                        collection.insertMany(userBatch);
                        totalInserted += userBatch.size();
                        long batchTime = System.currentTimeMillis() - currentBatchStartTime;
                        System.out.println("Batch inserted in " + batchTime + "ms. Total inserted so far: " + totalInserted);
                        userBatch.clear();
                        currentBatchStartTime = System.currentTimeMillis();
                    }
                }
            }
            System.out.println("Seed-Login: Successfully inserted " + totalInserted + " user documents into " + DbManager.COLLECTION_NAME + " in database " + DbManager.DB_NAME + ".");

        } catch (Exception e) {
            System.err.println("Seed-Login: An error occurred during database seeding into " + DbManager.DB_NAME + "." + DbManager.COLLECTION_NAME + ": " + e.getMessage());
            e.printStackTrace();
            DbManager.closeConnection();
            System.exit(1);
        } finally {
            DbManager.closeConnection();
        }
        System.out.println("Seed-Login (Java) finished in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}