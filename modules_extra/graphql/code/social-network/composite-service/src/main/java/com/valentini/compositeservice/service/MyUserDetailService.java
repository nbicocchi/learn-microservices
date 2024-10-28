package com.valentini.compositeservice.service;

import com.valentini.compositeservice.model.User;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Slf4j
@NoArgsConstructor
@Service
public class MyUserDetailService implements UserDetailsService {

    @Value("${application.api.key}")
    private String api_key;

    /**
     * Loads the user by username.
     *
     * @param username the username to search for
     * @return the UserDetails object
     * @throws UsernameNotFoundException if the username is not found
     */
    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-Key", api_key);

        String queryUser = "{ getUserByUsername(username: \"" + username + "\") { username password } }";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", queryUser);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("http://user:7001/graphql", request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            // Check for errors in the response
            if (responseBody.containsKey("errors")) {
                throw new Exception("Error retrieving user data");
            }

            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null) {
                throw new Exception("Error retrieving user data");
            }
            Map<String, Object> retrievedUser = (Map<String, Object>) data.get("getUserByUsername");
            if (retrievedUser == null) {
                throw new Exception("Error retrieving user data");
            }
            return org.springframework.security.core.userdetails.User.builder()
                    .username(retrievedUser.get("username").toString())
                    .password(retrievedUser.get("password").toString())
                    .build();
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new BadCredentialsException("Invalid API Key");
        } else {
            throw new UsernameNotFoundException(username);
        }

    }
}
