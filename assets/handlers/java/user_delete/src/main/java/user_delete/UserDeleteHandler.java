package user_delete;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

public class UserDeleteHandler {

    public static long deleteUserById(long id) {
        try {
            MongoCollection<Document> collection = DbManager.getUserCollection();
            Document filter = new Document("_id", id);
            System.out.println("Attempting to delete user with _id: " + id);
            DeleteResult result = collection.deleteOne(filter);
            long deletedCount = result.getDeletedCount();
            System.out.println("Documents deleted: " + deletedCount);
            return deletedCount;
        } catch (Exception e) {
            System.err.println("Error deleting user with id " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}