package user_create;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
public class User {

    private final String username;
    private final String email;
    private final String plainPassword;
    private String passwordHash;
    private Instant createdAt;

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("email") String email,
                @JsonProperty("password") String plainPassword) {
        this.username = username;
        this.email = email;
        this.plainPassword = plainPassword;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    String getPlainPassword() { return plainPassword; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }

    void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}