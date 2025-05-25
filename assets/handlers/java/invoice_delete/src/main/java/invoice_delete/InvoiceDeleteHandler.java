package invoice_delete;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

public class InvoiceDeleteHandler {

    /**
     * Löscht eine Rechnung anhand ihrer ID aus der Datenbank.
     *
     * @param id Die ID der zu löschenden Rechnung (als long).
     * @return Die Anzahl der gelöschten Dokumente (sollte 0 oder 1 sein).
     * @throws RuntimeException Wenn der Datenbankzugriff fehlschlägt.
     */
    public static long deleteInvoiceById(long id) {
        try {
            MongoCollection<Document> collection = DbManager.getInvoiceCollection();
            Document filter = new Document("_id", id);
            System.out.println("Attempting to delete invoice with _id: " + id);
            DeleteResult result = collection.deleteOne(filter);
            long deletedCount = result.getDeletedCount();
            System.out.println("Documents deleted: " + deletedCount);
            return deletedCount;
        } catch (Exception e) {
            System.err.println("Error deleting invoice with id " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete invoice", e);
        }
    }
}
