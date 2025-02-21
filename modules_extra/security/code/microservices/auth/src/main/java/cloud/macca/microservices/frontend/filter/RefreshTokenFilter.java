package cloud.macca.microservices.frontend.filter;

import cloud.macca.microservices.frontend.dto.AccessTokenResponse;
import cloud.macca.microservices.frontend.error.AuthorizationBadRequestError;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class RefreshTokenFilter extends OncePerRequestFilter {

    @Value("${auth.jwks_uri}")
    private String jwkEndpoint;
    @Value("${auth.endpoint}")
    private String authEndpoint;
    @Value("${auth.client_id}")
    private String clientId;
    @Value("${auth.client_secret}")
    private String clientSecret;

    private final RestClient http;

    public RefreshTokenFilter(
            RestClient.Builder builder
    ){
        this.http = builder.build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/profile");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // we want to refresh the access token, if any, if expired!

        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays.stream(cookies).filter(c -> c.getName().equals("refresh_token")).map(Cookie::getValue).toList().get(0);
        String accessToken = Arrays.stream(cookies).filter(c -> c.getName().equals("access_token")).map(Cookie::getValue).toList().get(0);

        if(refreshToken == null || accessToken == null){
            // keep the request going, it will eventually fail if the accessToken is null!
            filterChain.doFilter(request, response);
        }
        JwkProvider jwkProvider = new JwkProviderBuilder(new URL(jwkEndpoint))
                .build();
        Algorithm algo = getAlgorithm(jwkProvider);
        try{
            JWT.require(algo).build().verify(accessToken);
        }catch(JWTVerificationException e){
            Logger.getGlobal().info(e.getMessage());
            if(e.getMessage().contains("The Token has expired")){
                Logger.getGlobal().info("token refresh");
                // if the token is expired, then refresh it using the oauth2 refresh token mechanism
                MultiValueMap<String, String> reqBody = new LinkedMultiValueMap<>();
                reqBody.add("client_id", clientId);
                reqBody.add("client_secret", clientSecret);
                reqBody.add("grant_type", "refresh_token");
                reqBody.add("refresh_token", refreshToken);

                AccessTokenResponse refreshTokenResponse = this.http
                        .post()
                        .uri(authEndpoint + "/protocol/openid-connect/token")
                        .body(reqBody)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                            throw new AuthorizationBadRequestError(new String(res.getBody().readAllBytes()));
                        })
                        .body(AccessTokenResponse.class);
                List<Cookie> cookieList = Arrays.stream(request.getCookies())
                        .filter(cookie -> Objects.equals(cookie.getName(), "access_token"))
                                .toList();
                Cookie accessTokenCookie = cookieList.get(0);
                if(accessTokenCookie != null){
                    accessTokenCookie.setValue(refreshTokenResponse.accessToken);
                    response.addCookie(accessTokenCookie);
                     // refresh the page so the cookie value is updated
                    response.sendRedirect(request.getServletPath());
                }
            }
        }finally{
            // keep the request going, it will eventually fail if the token doesn't get renewed
            filterChain.doFilter(request, response);
        }

    }

    private static Algorithm getAlgorithm(JwkProvider jwkProvider) {
        RSAKeyProvider keyProvider = new RSAKeyProvider() {
            // we just need to implement this because we know that we will only
            // use this provider to verify jwts!
            @Override
            public RSAPublicKey getPublicKeyById(String s) {
                try {
                    return (RSAPublicKey) jwkProvider.get(s).getPublicKey();
                } catch (JwkException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return null;
            }

            @Override
            public String getPrivateKeyId() {
                return null;
            }
        };

        Algorithm algo = Algorithm.RSA256(keyProvider);
        return algo;
    }

}