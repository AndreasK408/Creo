package register;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;

public class UserCreator {

    public static String createUser(String username, String email, String hashedPassword) {
        MongoCollection<Document> collection = DbManager.getRegisterCollection();
        Document newUserDoc = new Document()
                .append("username", username)
                .append("email", email)
                .append("password_hash", hashedPassword)
                .append("created_at", Instant.now());
        try {
            System.out.println("Inserting new user: " + username);
            InsertOneResult result = collection.insertOne(newUserDoc);
            if (result.getInsertedId() == null) {
                throw new RuntimeException("Database insertion did not return an ID for new user.");
            }
            ObjectId insertedId = result.getInsertedId().asObjectId().getValue();
            System.out.println("User successfully created with ID: " + insertedId.toHexString());
            return insertedId.toHexString();
        } catch (Exception e) {
            System.err.println("Error creating user " + username + ": " + e.getMessage());
            throw new RuntimeException("Failed to create user in database", e);
        }
    }
}