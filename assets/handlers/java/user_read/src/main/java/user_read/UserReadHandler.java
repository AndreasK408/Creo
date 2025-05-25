package user_read;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UserReadHandler {

    public static UserReadResponse readUserById(long id) {
        try {
            MongoCollection<Document> collection = DbManager.getUserCollection();
            Document filter = new Document("_id", id);
            System.out.println("Attempting to read user with _id: " + id);
            Document userDoc = collection.find(filter).first();
            if (userDoc == null) {
                System.out.println("User with _id: " + id + " not found.");
                return null;
            }
            System.out.println("User with _id: " + id + " found. Mapping to response object.");
            return UserReadResponse.fromDocument(userDoc);
        } catch (Exception e) {
            System.err.println("Error reading user with id " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to read user", e);
        }
    }
}