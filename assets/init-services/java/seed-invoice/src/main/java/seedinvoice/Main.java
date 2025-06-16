package seedinvoice;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int DEFAULT_SEED_COUNT = 1000; // Fallback
    private static final int BATCH_SIZE = 5000;     // Wie in Python/Rust

    public static void main(String[] args) {
        System.out.println("Seed-Invoice (Java) starting...");
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
                System.out.println("Seed-Invoice (Java) finished in " + (System.currentTimeMillis() - startTime) + "ms.");
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
            collection = DbManager.getInvoiceCollection(); // Holt die invoice_collection

            List<Document> invoiceBatch = new ArrayList<>(BATCH_SIZE);
            int totalInserted = 0;
            long currentBatchStartTime = System.currentTimeMillis();

            for (int i = 1; i <= seedCount; i++) {
                Invoice invoice = new Invoice(i);
                invoiceBatch.add(invoice.toDocument());

                if (invoiceBatch.size() >= BATCH_SIZE || i == seedCount) {
                    if (!invoiceBatch.isEmpty()) {
                        System.out.println("Inserting batch of " + invoiceBatch.size() + " invoices... (up to id " + i + ")");
                        collection.insertMany(invoiceBatch);
                        totalInserted += invoiceBatch.size();
                        long batchTime = System.currentTimeMillis() - currentBatchStartTime;
                        System.out.println("Batch inserted in " + batchTime + "ms. Total inserted so far: " + totalInserted);
                        invoiceBatch.clear();
                        currentBatchStartTime = System.currentTimeMillis();
                    }
                }
            }
            System.out.println("Seed-Invoice: Successfully inserted " + totalInserted + " invoice documents.");

        } catch (Exception e) {
            System.err.println("Seed-Invoice: An error occurred during database seeding: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            DbManager.closeConnection();
        }
        System.out.println("Seed-Invoice (Java) finished in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
     