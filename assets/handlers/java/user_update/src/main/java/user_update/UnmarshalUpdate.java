package user_update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class UnmarshalUpdate {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static UserUpdatePayload unmarshalUser(byte[] jsonData) throws IOException {
        try {
            return objectMapper.readValue(jsonData, UserUpdatePayload.class);
        } catch (IOException e) {
            System.err.println("Error parsing JSON for update: " + e.getMessage());
            throw e;
        }
    }
}