package seeduser;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int DEFAULT_SEED_COUNT = 1000;
    private static final int BATCH_SIZE = 5000;

    public static void main(String[] args) {
        System.out.println("Seed-User (Java) starting...");

        int seedCount;
        try {
            String seedCountEnv = System.getenv("MG_SEED_COUNT");
            if (seedCountEnv != null && !seedCountEnv.isEmpty()) {
                seedCount = Integer.parseInt(seedCountEnv);
            } else {
                System.out.println("MG_SEED_COUNT not set or empty, using default: " + DEFAULT_SEED_COUNT);
                seedCount = DEFAULT_SEED_COUNT;
            }
            if (seedCount <= 0) {
                System.out.println("Seed count is " + seedCount + ", no records will be inserted.");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing MG_SEED_COUNT environment variable: " + e.getMessage() + ". Using default: " + DEFAULT_SEED_COUNT);
            seedCount = DEFAULT_SEED_COUNT;
        }

        System.out.println("Target seed count: " + seedCount);
        System.out.println("Batch size: " + BATCH_SIZE);

        MongoCollection<Document> collection = null;
        try {
            collection = DbManager.getUserCollection();

            List<Document> userBatch = new ArrayList<>(BATCH_SIZE);
            int totalInserted = 0;

            for (int i = 1; i <= seedCount; i++) {
                User user = new User(i); // _id wird als long i gesetzt
                userBatch.add(user.toDocument());

                if (userBatch.size() == BATCH_SIZE || i == seedCount) {
                    if (!userBatch.isEmpty()) {
                        System.out.println("Inserting batch of " + userBatch.size() + " users... (up to id " + i + ")");
                        collection.insertMany(userBatch);
                        totalInserted += userBatch.size();
                        System.out.println("Batch inserted. Total inserted so far: " + totalInserted);
                        userBatch.clear();
                    }
                }
            }
            System.out.println("Seed-User: Successfully inserted " + totalInserted + " user documents.");

        } catch (Exception e) {
            System.err.println("Seed-User: An error occurred during database seeding: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Beende mit Fehlercode
        } finally {
            DbManager.closeConnection(); // Wichtig, um Ressourcen freizugeben
        }
        System.out.println("Seed-User (Java) finished.");
    }
}
     