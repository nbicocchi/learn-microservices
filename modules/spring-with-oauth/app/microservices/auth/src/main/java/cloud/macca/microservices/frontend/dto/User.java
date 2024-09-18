package cloud.macca.microservices.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("name")
    public final String fullName;
    @JsonProperty("email")
    public final String email;
    @JsonProperty("sub")
    public final String id;
    @JsonProperty("email_verified")
    public final boolean emailVerified;
    public User(String id, String fullName, String email, boolean emailVerified){
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.emailVerified = emailVerified;
    }
}
