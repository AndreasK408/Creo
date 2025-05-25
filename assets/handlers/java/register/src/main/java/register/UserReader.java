package register;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UserReader {

    private static UserDbRecord readUserByKey(String key, String value) {
        MongoCollection<Document> collection = DbManager.getRegisterCollection();
        Document query = new Document(key, value);
        Document userDoc = collection.find(query).first();
        return UserDbRecord.fromDocument(userDoc);
    }
    public static UserDbRecord readUserByUsername(String username) {
        System.out.println("Checking existence by username: " + username);
        return readUserByKey("username", username);
    }
    public static UserDbRecord readUserByEmail(String email) {
        System.out.println("Checking existence by email: " + email);
        return readUserByKey("email", email);
    }
}