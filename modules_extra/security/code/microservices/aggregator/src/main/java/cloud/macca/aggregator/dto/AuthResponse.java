package cloud.macca.aggregator.dto;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName(value = "access_token")
    public final String accessToken;
    @SerializedName(value = "expires_in")
    public final int expiresIn;
    @SerializedName(value = "refresh_expires_in")
    public final int refreshExpiresIn;
    @SerializedName(value = "token_type")
    public final String tokenType;
    @SerializedName(value = "not-before-policy")
    public final int notBefore;
    public final String scope;
    public AuthResponse(
            String accessToken,
            int expiresIn,
            int refreshExpiresIn,
            String tokenType,
            int notBefore,
            String scope
    ){
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.tokenType = tokenType;
        this.notBefore = notBefore;
        this.scope = scope;
    }
}
