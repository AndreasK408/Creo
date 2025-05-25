package user_create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class Unmarshal {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static User unmarshalUser(byte[] jsonData) throws IOException {
        try {
            return objectMapper.readValue(jsonData, User.class);
        } catch (IOException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            throw e;
        }
    }
}