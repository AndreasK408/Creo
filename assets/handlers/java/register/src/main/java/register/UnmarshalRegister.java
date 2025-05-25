package register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class UnmarshalRegister {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule());

    public static CreateUserInput unmarshalUser(byte[] jsonData) throws IOException {
        try {
            return objectMapper.readValue(jsonData, CreateUserInput.class);
        } catch (IOException e) {
            System.err.println("Error parsing JSON for registration: " + e.getMessage());
            throw e;
        }
    }
}