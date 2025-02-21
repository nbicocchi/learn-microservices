package cloud.macca.microservices.frontend.controller;

import cloud.macca.microservices.frontend.dto.AccessTokenResponse;
import cloud.macca.microservices.frontend.dto.SuccessResponse;
import cloud.macca.microservices.frontend.error.AuthorizationBadRequestError;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.logging.Logger;


@RestController
@RequestMapping(value = "/auth")
public class MainController {

    private final RestClient http;

    @Value("${auth.endpoint}")
    private String authEndpoint;

    @Value("${auth.client_id}")
    private String clientId;

    @Value("${auth.client_secret}")
    private String clientSecret;

    @Value("${auth.redirect_uri}")
    private String redirectUri;

    public MainController(
            RestClient.Builder builder,
            @Value("${auth.endpoint}") String authEndpoint
    ){
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(authEndpoint);
        this.http = builder.uriBuilderFactory(factory).build();
    }

    @GetMapping(value = "/init-login")
    public RedirectView beginLoginSession(){
        return new RedirectView(authEndpoint + "/protocol/openid-connect/auth?response_type=code&scope=openid&client_id=" + clientId + "&redirect_uri=" + redirectUri);
    }

    @GetMapping(value = "/test-no-filter")
    public SuccessResponse<String> exampleNoFilter(){
        return new SuccessResponse<String>("hello non filtered!");
    }

    @GetMapping(value = "/code")
    public RedirectView retrieveAccessToken(
            @RequestParam(value = "code") String code,
            HttpServletResponse r
    ) throws Exception {
        // code is the session token we use to generate the access token jwt
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        AccessTokenResponse response = this.http.post()
                .uri(authEndpoint + "/protocol/openid-connect/token")
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new AuthorizationBadRequestError(new String(res.getBody().readAllBytes()));
                })
                .body(AccessTokenResponse.class);

        if(response == null){
            throw new Exception("something went wrong while requesting the URL!");
        }

        Cookie accessTokenCookie = new Cookie("access_token", response.accessToken);
        Cookie refreshTokenCookie = new Cookie("refresh_token", response.refreshToken);

        accessTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        refreshTokenCookie.setPath("/");
        accessTokenCookie.setAttribute("SameSite", "Lax");
        refreshTokenCookie.setAttribute("SameSite", "Lax");

        r.addCookie(accessTokenCookie);
        r.addCookie(refreshTokenCookie);

        return new RedirectView("/profile/");

    }

}
