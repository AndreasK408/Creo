package user_update;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.io.IOException;

public class UserUpdateHandler {

    public static long updateUserById(long id, byte[] jsonData) throws IOException {
        UserUpdatePayload payload = UnmarshalUpdate.unmarshalUser(jsonData);
        Document updateFields = new Document();
        if (payload.getUsername() != null) {
            updateFields.append("username", payload.getUsername());
        }
        if (payload.getEmail() != null) {
            updateFields.append("email", payload.getEmail());
        }
        if (payload.getPlainPassword() != null) {
            String hashedPassword = PasswordHasher.hashPassword(payload.getPlainPassword());
            // Wichtig! Feldname in der datenbank ist "password_hash"
            updateFields.append("password_hash", hashedPassword);
        }
        if (!updateFields.isEmpty()) {
            try {
                MongoCollection<Document> collection = DbManager.getUserCollection();
                Document filter = new Document("_id", id);
                Document updateOperation = new Document("$set", updateFields);
                System.out.println("Attempting to update user with _id: " + id + " with fields: " + updateFields.keySet());
                UpdateResult result = collection.updateOne(filter, updateOperation);
                long modifiedCount = result.getModifiedCount();
                System.out.println("Update result - Matched: " + result.getMatchedCount() + ", Modified: " + modifiedCount);
                return modifiedCount;
            } catch (Exception e) {
                System.err.println("Error updating user with id " + id + ": " + e.getMessage());
                throw new RuntimeException("Failed to update user", e);
            }
        } else {
            System.out.println("No fields to update for user with _id: " + id);
            return 0L;
        }
    }
}