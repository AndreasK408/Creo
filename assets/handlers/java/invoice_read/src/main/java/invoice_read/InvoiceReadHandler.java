package invoice_read;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class InvoiceReadHandler {

    /**
     * Liest eine Rechnung anhand ihrer ID aus der Datenbank.
     *
     * @param id Die ID der zu lesenden Rechnung (als long).
     * @return Ein InvoiceReadResponse-Objekt mit den Rechnungsdaten oder null, wenn keine Rechnung gefunden wurde.
     * @throws RuntimeException Wenn der Datenbankzugriff fehlschlägt.
     */
    public static InvoiceReadResponse readInvoiceById(long id) {
        try {
            MongoCollection<Document> collection = DbManager.getInvoiceCollection();
            Document filter = new Document("_id", id);
            System.out.println("Attempting to read invoice with _id: " + id);
            Document invoiceDoc = collection.find(filter).first();
            if (invoiceDoc == null) {
                System.out.println("Invoice with _id: " + id + " not found.");
                return null;
            }
            System.out.println("Invoice with _id: " + id + " found. Mapping to response object.");
            return InvoiceReadResponse.fromDocument(invoiceDoc);
        } catch (Exception e) {
            System.err.println("Error reading invoice with id " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to read invoice", e);
        }
    }
}