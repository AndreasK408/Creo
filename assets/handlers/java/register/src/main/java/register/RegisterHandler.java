package register;

import java.io.IOException;

public class RegisterHandler {

    public static String registerUser(byte[] jsonData) throws IOException {
        System.out.println("Register user request received.");
        CreateUserInput userInput = UnmarshalRegister.unmarshalUser(jsonData);
        if (UserReader.readUserByEmail(userInput.getEmail()) != null) {
            System.out.println("Registration failed: Email '" + userInput.getEmail() + "' already exists.");
            return null;
        }
        if (UserReader.readUserByUsername(userInput.getUsername()) != null) {
            System.out.println("Registration failed: Username '" + userInput.getUsername() + "' already exists.");
            return null;
        }
        String hashedPassword = PasswordHasher.hashPassword(userInput.getPlainPassword());
        return UserCreator.createUser(userInput.getUsername(), userInput.getEmail(), hashedPassword);
    }
}
