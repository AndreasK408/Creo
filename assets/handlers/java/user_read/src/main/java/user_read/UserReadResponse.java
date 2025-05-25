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
        ObjectId objectId = doc.getObjectId("_id");
        String idString = (objectId != null) ? objectId.toHexString() : null;
        String username = doc.getString("username");
        String email = doc.getString("email");
        long createdAtTimestamp = 0; // Standardwert
        Object createdAtObj = doc.get("created_at");
        if (createdAtObj instanceof Date) {
            createdAtTimestamp = ((Date) createdAtObj).getTime() / 1000L;
        } else if (createdAtObj instanceof Instant) {
            createdAtTimestamp = ((Instant) createdAtObj).getEpochSecond();
        } else if (createdAtObj instanceof Long) {
            // Falls es bereits als Long (Sekunden oder Millisekunden) gespeichert ist
            // Hier gehe ich von Sekunden aus, ansonsten sind Anpassung nötig
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