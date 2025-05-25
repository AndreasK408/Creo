package invoice_update;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InvoiceUpdateHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule());

    /**
     * Aktualisiert eine Rechnung anhand ihrer ID mit den bereitgestellten Daten.
     *
     * @param id Die ID der zu aktualisierenden Rechnung (als long).
     * @param jsonData Die rohen JSON-Daten als Byte-Array mit den Update-Feldern.
     * @return long Die Anzahl der tatsächlich modifizierten Dokumente (0 oder 1).
     * @throws IOException Wenn das JSON-Parsing fehlschlägt.
     * @throws RuntimeException Wenn der DB-Zugriff fehlschlägt.
     */
    public static long updateInvoiceById(long id, byte[] jsonData) throws IOException {
        InvoiceUpdatePayload payload = UnmarshalInvoiceUpdate.unmarshalInvoice(jsonData);
        Document updateFields = new Document();
        try {
            if (payload.getItems() != null) {
                List<Map<String, Object>> itemsMapList = objectMapper.convertValue(payload.getItems(), new TypeReference<List<Map<String, Object>>>() {});
                updateFields.append("items", itemsMapList);
            }
            if (payload.getBillingAddress() != null) {
                Map<String, Object> billingAddressMap = objectMapper.convertValue(payload.getBillingAddress(), new TypeReference<Map<String, Object>>() {});
                updateFields.append("billing_address", billingAddressMap);
            }
            if (payload.getShippingAddress() != null) {
                Map<String, Object> shippingAddressMap = objectMapper.convertValue(payload.getShippingAddress(), new TypeReference<Map<String, Object>>() {});
                updateFields.append("shipping_address", shippingAddressMap);
            }
            if (payload.getTaxRate() != null) {
                updateFields.append("tax_rate", payload.getTaxRate());
            }
            if (payload.getExtraInfo() != null) {
                updateFields.append("extra_info", payload.getExtraInfo());
            }
            if (payload.getStatus() != null) {
                updateFields.append("status", payload.getStatus().name());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error converting InvoiceUpdatePayload to Document: " + e.getMessage());
            throw new RuntimeException("Failed to prepare invoice update data for DB", e);
        }


        // Datenbank-Update durchführen (nur wenn Felder zu aktualisieren sind)
        if (!updateFields.isEmpty()) {
            try {
                MongoCollection<Document> collection = DbManager.getInvoiceCollection();
                Document filter = new Document("_id", id);
                Document updateOperation = new Document("$set", updateFields);
                System.out.println("Attempting to update invoice with _id: " + id + " with fields: " + updateFields.keySet());
                UpdateResult result = collection.updateOne(filter, updateOperation);
                long modifiedCount = result.getModifiedCount();
                System.out.println("Update result - Matched: " + result.getMatchedCount() + ", Modified: " + modifiedCount);

                return modifiedCount;
            } catch (Exception e) {
                System.err.println("Error updating invoice with id " + id + ": " + e.getMessage());
                throw new RuntimeException("Failed to update invoice", e);
            }
        } else {
            System.out.println("No fields to update for invoice with _id: " + id);
            return 0L;
        }
    }
}