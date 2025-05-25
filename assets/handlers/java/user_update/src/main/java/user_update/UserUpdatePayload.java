package user_update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

public class UserUpdatePayload {
    private final String username;
    private final String email;
    private final String plainPassword;

    @JsonCreator
    public UserUpdatePayload(
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