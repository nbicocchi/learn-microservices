package cloud.macca.microservices.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

    @JsonProperty("access_token")
    public final String accessToken;
    @JsonProperty("expires_in")
    public final int expiresIn;
    @JsonProperty("refresh_expires_in")
    public final int refreshExpiresIn;
    @JsonProperty("refresh_token")
    public final String refreshToken;
    @JsonProperty("token_type")
    public final String tokenType;
    @JsonProperty("not-before-policy")
    public final int notBefore;
    @JsonProperty("session_state")
    public final String sessionState;
    public final String scope;

    public AccessTokenResponse(
            String accessToken,
            int expiresIn,
            int refreshExpiresIn,
            String refreshToken,
            String tokenType,
            int notBefore,
            String sessionState,
            String scope
    ){
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.notBefore = notBefore;
        this.sessionState = sessionState;
        this.scope = scope;
    }

}


