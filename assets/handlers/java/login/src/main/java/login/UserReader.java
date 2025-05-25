package login;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UserReader {

    private static UserLoginData readUserByKey(String key, String value) {
        MongoCollection<Document> collection = DbManager.getLoginCollection();
        Document query = new Document(key, value);
        Document userDoc = collection.find(query).first();
        return UserLoginData.fromDocument(userDoc);
    }

    public static UserLoginData readUserByUsername(String username) {
        System.out.println("Reading user by username: " + username);
        return readUserByKey("username", username);
    }

    public static UserLoginData readUserByEmail(String email) {
        System.out.println("Reading user by email: " + email);
        return readUserByKey("email", email);
    }
}