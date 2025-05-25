package login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class UserLoginData {
    private final String id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;
    UserLoginData(String id, String username, String email, String passwordHash, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public static UserLoginData fromDocument(Document doc) {
        if (doc == null) return null;
        ObjectId objectId = doc.getObjectId("_id");
        String idStr = (objectId != null) ? objectId.toHexString() : doc.getString("id");

        // In Python wird password_hash als bytes gespeichert, ich gehe davon aus,
        // dass der PHC-String in der datenbank gespeichert wird.
        String passwordHashStr = doc.getString("password_hash");
        if (passwordHashStr == null && doc.get("password_hash") instanceof org.bson.BsonBinary) {
            // Absicherung, falls es doch als BsonBinary gespeichert wird
            passwordHashStr = new String(((org.bson.BsonBinary) doc.get("password_hash")).getData());
        }


        Instant createdAtInstant = null;
        Object createdAtObj = doc.get("created_at");
        if (createdAtObj instanceof Date) {
            createdAtInstant = ((Date) createdAtObj).toInstant();
        } else if (createdAtObj instanceof Instant) {
            createdAtInstant = (Instant) createdAtObj;
        } else if (createdAtObj instanceof Long) {
            createdAtInstant = Instant.ofEpochSecond((Long) createdAtObj);
        }
        return new UserLoginData(
                idStr,
                doc.getString("username"),
                doc.getString("email"),
                passwordHashStr,
                createdAtInstant
        );
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}

class SessionData {
    final String userId;
    final String sessionId;
    final Instant expiresAt; // exp in Python

    SessionData(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.expiresAt = Instant.now().plus(3, ChronoUnit.DAYS);
    }
    public String getUserId() { return userId; }
    public String getSessionId() { return sessionId; }
    public Instant getExpiresAt() { return expiresAt; }
}

public class SessionResponse {
    @JsonProperty("token")
    private final String token;
    @JsonProperty("exp")
    private final long expiresAtTimestamp;

    public SessionResponse(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAtTimestamp = expiresAt.getEpochSecond();
    }

    public String getToken() { return token; }
    public long getExpiresAtTimestamp() { return expiresAtTimestamp; }
}