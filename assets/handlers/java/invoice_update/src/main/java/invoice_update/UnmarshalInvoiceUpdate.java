package invoice_update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class UnmarshalInvoiceUpdate {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule());

    public static InvoiceUpdatePayload unmarshalInvoice(byte[] jsonData) throws IOException {
        try {
            return objectMapper.readValue(jsonData, InvoiceUpdatePayload.class);
        } catch (IOException e) {
            System.err.println("Error parsing JSON for invoice update: " + e.getMessage());
            throw e;
        }
    }
}