package invoice_create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class UnmarshalInvoice {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule());


    public static Invoice unmarshalInvoice(byte[] jsonData) throws IOException {
        try {
            return objectMapper.readValue(jsonData, Invoice.class);
        } catch (IOException e) {
            System.err.println("Error parsing JSON for invoice: " + e.getMessage());
            throw e;
        }
    }
}