package seedregister;

import com.github.javafaker.Faker;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.bson.Document;

import java.time.Instant;
import java.util.Locale;
import java.util.Date;

public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private Instant createdAt;

    private static final Faker faker = new Faker(new Locale("en-US"));
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    private static final int HASH_ITERATIONS = 1;
    private static final int HASH_MEMORY_KB = 1024;
    private static final int HASH_PARALLELISM = 1;
    private static final int DUMMY_PASSWORD_LENGTH = 12;

    public User(long id) {
        this.id = id;
        String rawUsernameFaker = faker.name().username();
        String cleanedUsername = rawUsernameFaker.replaceAll("[^a-zA-Z0-9.-]", "");
        if (cleanedUsername.trim().isEmpty()) {
            cleanedUsername = "user" + id;
        }
        this.username = cleanedUsername.substring(0, Math.min(cleanedUsername.length(), 30));

        this.email = faker.internet().emailAddress();
        char[] dummyPasswordChars = faker.lorem().characters(DUMMY_PASSWORD_LENGTH, DUMMY_PASSWORD_LENGTH + 5, true, true).toCharArray();
        try {
            this.passwordHash = argon2.hash(HASH_ITERATIONS, HASH_MEMORY_KB, HASH_PARALLELISM, dummyPasswordChars);
        } finally {
            argon2.wipeArray(dummyPasswordChars);
        }
        this.createdAt = Instant.now();
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }

    public org.bson.Document toDocument() {
        return new org.bson.Document("_id", this.id)
                .append("username", this.username)
                .append("email", this.email)
                .append("password_hash", this.passwordHash)
                .append("created_at", java.util.Date.from(this.createdAt));
    }
}