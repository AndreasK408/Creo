package seedregister;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int DEFAULT_SEED_COUNT = 1000;
    private static final int BATCH_SIZE = 5000;

    public static void main(String[] args) {
        System.out.println("Seed-Register (Java) starting...");
        long startTime = System.currentTimeMillis();

        int seedCount;
        try {
            String seedCountEnv = System.getenv("MG_SEED_COUNT");
            if (seedCountEnv != null && !seedCountEnv.trim().isEmpty()) {
                seedCount = Integer.parseInt(seedCountEnv);
            } else {
                System.out.println("INFO: MG_SEED_COUNT environment variable not set or empty, using default: " + DEFAULT_SEED_COUNT);
                seedCount = DEFAULT_SEED_COUNT;
            }
            if (seedCount <= 0) {
                System.out.println("INFO: Seed count is " + seedCount + ", no records will be inserted.");
                System.out.println("Seed-Register (Java) finished in " + (System.currentTimeMillis() - startTime) + "ms.");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Could not parse MG_SEED_COUNT environment variable: '" + System.getenv("MG_SEED_COUNT") + "'. " + e.getMessage() + ". Using default: " + DEFAULT_SEED_COUNT);
            seedCount = DEFAULT_SEED_COUNT;
        }

        System.out.println("Target seed count: " + seedCount);
        System.out.println("Batch size: " + BATCH_SIZE);

        MongoCollection<Document> collection = null;
        try {
            collection = DbManager.getRegisterCollection();
            try {
                System.out.println("Seed-Register: Ensuring indexes on 'username' and 'email'...");
                collection.createIndex(Indexes.ascending("username"));
                collection.createIndex(Indexes.ascending("email"));
                System.out.println("Seed-Register: Indexes ensured.");
            } catch (Exception e) {
                System.err.println("Seed-Register: Warning - Could not create indexes (they might already exist or DB user lacks permissions): " + e.getMessage());
            }


            List<Document> userBatch = new ArrayList<>(BATCH_SIZE);
            int totalInserted = 0;
            long currentBatchStartTime = System.currentTimeMillis();

            for (int i = 1; i <= seedCount; i++) {
                User user = new User(i);
                userBatch.add(user.toDocument());

                if (userBatch.size() >= BATCH_SIZE || i == seedCount) {
                    if (!userBatch.isEmpty()) {
                        System.out.println("Inserting batch of " + userBatch.size() + " users into register_collection... (up to id " + i + ")");
                        collection.insertMany(userBatch);
                        totalInserted += userBatch.size();
                        long batchTime = System.currentTimeMillis() - currentBatchStartTime;
                        System.out.println("Batch inserted in " + batchTime + "ms. Total inserted so far: " + totalInserted);
                        userBatch.clear();
                        currentBatchStartTime = System.currentTimeMillis();
                    }
                }
            }
            System.out.println("Seed-Register: Successfully inserted " + totalInserted + " user documents into register_collection.");

        } catch (Exception e) {
            System.err.println("Seed-Register: An error occurred during database seeding: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            DbManager.closeConnection();
        }
        System.out.println("Seed-Register (Java) finished in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
     