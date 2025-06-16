package seeduser;

import com.github.javafaker.Faker;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.bson.Document;

import java.time.Instant;
import java.util.Locale;

public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private Instant createdAt;

    private static final Faker faker = new Faker(new Locale("en-US"));
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    private static final int DUMMY_PASSWORD_LENGTH = 12;
    private static final int SEEDER_HASH_ITERATIONS = 1;
    private static final int SEEDER_HASH_MEMORY_KB = 1024;
    private static final int SEEDER_HASH_PARALLELISM = 1;

    public User(long id) {
        this.id = id;
        String rawUsername = faker.name().username();
        if (rawUsername == null) {
            rawUsername = "defaultuser" + id;
        }
        this.username = rawUsername.substring(0, Math.min(rawUsername.length(), 64));
        this.email = faker.internet().emailAddress();
        char[] dummyPassword = faker.lorem().characters(DUMMY_PASSWORD_LENGTH).toCharArray();
        this.passwordHash = argon2.hash(SEEDER_HASH_ITERATIONS, SEEDER_HASH_MEMORY_KB, SEEDER_HASH_PARALLELISM, dummyPassword);
        argon2.wipeArray(dummyPassword);
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
                .append("created_at", this.createdAt);
    }
}