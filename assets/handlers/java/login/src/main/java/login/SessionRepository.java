package login;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {
    private final Map<String, SessionData> sessionCache = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public SessionResponse setNewSession(String userId) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String sessionId = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        SessionData sessionData = new SessionData(userId, sessionId);
        sessionCache.put(sessionId, sessionData);
        System.out.println("New session created: " + sessionId + " for user: " + userId);
        return new SessionResponse(sessionId, sessionData.getExpiresAt());
    }

    // Optional: Methode zum Abrufen von Sessions (nicht im Python Code für login)
    public SessionData getSession(String sessionId) {
        SessionData data = sessionCache.get(sessionId);
        if (data != null && data.getExpiresAt().isAfter(Instant.now())) {
            return data;
        }
        if (data != null) {
            sessionCache.remove(sessionId);
            System.out.println("Removed expired session: " + sessionId);
        }
        return null;
    }

    private static class Holder {
        static final SessionRepository INSTANCE = new SessionRepository();
    }

    public static SessionRepository getInstance() {
        return Holder.INSTANCE;
    }

    private SessionRepository() {
        // Privater Konstruktor für singeleton
    }
}