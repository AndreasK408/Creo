package login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;
import java.util.regex.Pattern;

public class LoginHandler {
    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
    );

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static Map<String, Object> loginWithUsernameOrEmail(String usernameOrEmail, String password) {
        System.out.println("Login attempt for: " + usernameOrEmail);
        UserLoginData user;
        if (EMAIL_REGEX.matcher(usernameOrEmail).matches()) {
            user = UserReader.readUserByEmail(usernameOrEmail);
        } else {
            user = UserReader.readUserByUsername(usernameOrEmail);
        }
        if (user != null) {
            System.out.println("User found: " + user.getUsername());
            if (PasswordVerifier.verifyPassword(user.getPasswordHash(), password)) {
                System.out.println("Password verified for user: " + user.getUsername());
                SessionRepository repo = SessionRepository.getInstance();
                SessionResponse sessionResponse = repo.setNewSession(user.getId());
                try {
                    return objectMapper.convertValue(sessionResponse, new TypeReference<Map<String, Object>>() {});
                } catch (IllegalArgumentException e) {
                    System.err.println("Error converting SessionResponse to Map: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("Password verification failed for user: " + user.getUsername());
            }
        } else {
            System.out.println("User not found: " + usernameOrEmail);
        }
        return null;
    }
}