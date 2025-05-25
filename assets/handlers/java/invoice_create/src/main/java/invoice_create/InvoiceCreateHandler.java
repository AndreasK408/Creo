package invoice_create;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

public class InvoiceCreateHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule());

    /**
     * Erstellt eine neue Rechnung in der Datenbank.
     *
     * @param jsonData Die rohen JSON-Daten als Byte-Array.
     * @return Die generierte ObjectId der neuen Rechnung als String.
     * @throws IOException Wenn das JSON-Parsing fehlschlägt.
     * @throws RuntimeException Wenn der DB-Zugriff fehlschlägt.
     */
    public static String createInvoice(byte[] jsonData) throws IOException {

        // Unmarshalling (JSON -> Invoice Objekt)
        Invoice invoice = UnmarshalInvoice.unmarshalInvoice(jsonData);

        // Setze Standardwerte, falls sie im JSON fehlten (und von Jackson nicht gesetzt wurden)
        if (invoice.getTaxRate() == null) {
            invoice.setTaxRate(0.15);
        }
        if (invoice.getIssuedAt() == null) {
            invoice.setIssuedAt(Instant.now());
        }
        if (invoice.getExtraInfo() == null) {
            invoice.setExtraInfo("");
        }
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.OPEN);
        }

        // Konvertiere das Invoice-POJO in eine Map und dann in ein BSON Document
        Document invoiceDoc;
        try {
            Map<String, Object> invoiceMap = objectMapper.convertValue(invoice, new TypeReference<Map<String, Object>>() {});
            invoiceDoc = new Document(invoiceMap);
            if (invoiceDoc.containsKey("status")) {
                invoiceDoc.put("status", invoice.getStatus().name());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error converting Invoice POJO to Document: " + e.getMessage());
            throw new RuntimeException("Failed to prepare invoice data for DB", e);
        }

        // In Datenbank einfügen
        try {
            System.out.println("Attempting to insert invoice document...");
            var insertResult = DbManager.getInvoiceCollection().insertOne(invoiceDoc);

            if (insertResult.getInsertedId() == null) {
                throw new RuntimeException("Database insertion did not return an ID for invoice.");
            }

            // ID zurückgeben
            ObjectId insertedId = insertResult.getInsertedId().asObjectId().getValue();
            System.out.println("Invoice successfully inserted with ID: " + insertedId.toHexString());
            return insertedId.toHexString();

        } catch (Exception e) {
            System.err.println("Error during invoice database insertion: " + e.getMessage());
            throw new RuntimeException("Database insertion failed for invoice", e);
        }
    }
}