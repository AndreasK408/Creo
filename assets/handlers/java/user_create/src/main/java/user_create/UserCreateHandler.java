package user_create;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.Instant;

public class UserCreateHandler {
    public static String createUser(byte[] jsonData) throws IOException {
        User user = Unmarshal.unmarshalUser(jsonData);
        String hashedPassword = PasswordHasher.hashPassword(user.getPlainPassword());
        user.setPasswordHash(hashedPassword);

        user.setCreatedAt(Instant.now());
        try {
            Document userDoc = new Document()
                    .append("username", user.getUsername())
                    .append("email", user.getEmail())
                    .append("password_hash", user.getPasswordHash())
                    .append("created_at", user.getCreatedAt());
            var insertResult = DbManager.getUserCollection().insertOne(userDoc);
            if (insertResult.getInsertedId() == null) {
                throw new RuntimeException("Database insertion did not return an ID.");
            }
            ObjectId insertedId = insertResult.getInsertedId().asObjectId().getValue();
            return insertedId.toHexString();
        } catch (Exception e) {
            System.err.println("Error during database insertion: " + e.getMessage());
            throw new RuntimeException("Database insertion failed", e);
        }
    }
}