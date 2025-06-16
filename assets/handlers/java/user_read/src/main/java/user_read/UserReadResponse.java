package user_read;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Date;

public class UserReadResponse {
    private final String id;
    private final String username;
    private final String email;
    private final long createdAt;

    private UserReadResponse(String id, String username, String email, long createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static UserReadResponse fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }

        String idString = null;
        Object idObj = doc.get("_id");

        if (idObj instanceof Long) {
            idString = ((Long) idObj).toString();
        } else if (idObj instanceof ObjectId) {
            idString = ((ObjectId) idObj).toHexString();
            System.err.println("Hinweis in UserReadResponse.fromDocument: _id war eine ObjectId: " + idString);
        } else if (idObj != null) {
            // Für andere unerwartete Typen
            System.err.println("Warnung in UserReadResponse.fromDocument: _id hat einen unerwarteten Typ: " + idObj.getClass().getName() + ", Wert: " + idObj);
            idString = idObj.toString();
        }

        String username = doc.getString("username");
        String email = doc.getString("email");

        long createdAtTimestamp = 0;
        Object createdAtObj = doc.get("created_at");
        if (createdAtObj instanceof Date) {
            createdAtTimestamp = ((Date) createdAtObj).getTime() / 1000L;
        } else if (createdAtObj instanceof Instant) {
            createdAtTimestamp = ((Instant) createdAtObj).getEpochSecond();
        } else if (createdAtObj instanceof Long) {
            createdAtTimestamp = (Long) createdAtObj;
        }

        return new UserReadResponse(idString, username, email, createdAtTimestamp);
    }

    @JsonProperty("id")
    public String getId() { return id; }
    @JsonProperty("username")
    public String getUsername() { return username; }
    @JsonProperty("email")
    public String getEmail() { return email; }
    @JsonProperty("created_at")
    public long getCreatedAt() { return createdAt; }
}