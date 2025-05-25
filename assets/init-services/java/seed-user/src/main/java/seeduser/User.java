package seeduser;

import com.github.javafaker.Faker; // Für Zufallsdaten
import de.mkammerer.argon2.Argon2; // Für Dummy-Passwort-Hash
import de.mkammerer.argon2.Argon2Factory;

import java.time.Instant;
import java.util.Locale;

public class User {
    private long id; // Entspricht _id in Python/Rust (long für i64)
    private String username;
    private String email;
    private String passwordHash; // Speichert den PHC-String
    private Instant createdAt;

    private static final Faker faker = new Faker(new Locale("en-US")); // Oder eine andere Locale
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    private static final int DUMMY_PASSWORD_LENGTH = 12;


    // Konstruktor, um einen User mit zufälligen Daten zu erstellen
    public User(long id) {
        this.id = id;
        this.username = faker.name().username().substring(0, Math.min(faker.name().username().length(), 64)); // Begrenze Länge
        this.email = faker.internet().emailAddress();
        // Erzeuge einen Dummy-Passwort-Hash (nicht für echte Sicherheit gedacht)
        // Python erzeugt random bytes, Rust auch. Hier generieren wir einen echten (aber dummy) Hash.
        char[] dummyPassword = faker.lorem().characters(DUMMY_PASSWORD_LENGTH).toCharArray();
        this.passwordHash = argon2.hash(1, 1024, 1, dummyPassword); // Minimale Parameter für Geschwindigkeit
        argon2.wipeArray(dummyPassword); // Dummy-Passwort löschen
        this.createdAt = Instant.now();
    }

    // Getter (werden benötigt, um das Objekt in ein Document umzuwandeln)
    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }

    // Methode, um das User-Objekt in ein MongoDB Document umzuwandeln
    public org.bson.Document toDocument() {
        return new org.bson.Document("_id", this.id) // _id als Feldname in MongoDB
                .append("username", this.username)
                .append("email", this.email)
                .append("password_hash", this.passwordHash) // Speichert den PHC-String
                .append("created_at", this.createdAt); // Instant wird korrekt von MongoDB-Treiber behandelt
    }
}