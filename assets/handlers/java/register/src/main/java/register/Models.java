package register;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Date;

class CreateUserInput {
    private final String username;
    private final String email;
    private final String plainPassword;

    @JsonCreator
    public CreateUserInput(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("password") String plainPassword
    ) {
        this.username = username;
        this.email = email;
        this.plainPassword = plainPassword;
    }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPlainPassword() { return plainPassword; }
}

class UserDbRecord {
    private final String id;
    private final String username;
    private final String email;

    UserDbRecord(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public static UserDbRecord fromDocument(Document doc) {
        if (doc == null) return null;

        String idStr = null;
        Object idObj = doc.get("_id");

        if (idObj instanceof Long) {
            idStr = ((Long) idObj).toString();
        } else if (idObj instanceof ObjectId) {
            idStr = ((ObjectId) idObj).toHexString();
        } else if (idObj != null) {
            System.err.println("Warnung: UserDbRecord.fromDocument - _id hat einen unerwarteten Typ: " + idObj.getClass().getName() + ", Wert: " + idObj);
            idStr = idObj.toString();
        }

        return new UserDbRecord(
                idStr,
                doc.getString("username"),
                doc.getString("email")
        );
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}