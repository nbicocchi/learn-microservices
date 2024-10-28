package com.valentini.interactionservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER_NAME = "X-API-Key"; // Header name
    @Value("${application.api.key}")
    private static final String API_KEY_VALUE = "yourapikey"; // Replace with your actual API key

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Check if the request is for /graphql
        if ("/graphql".equals(request.getServletPath())) {
            String apiKey = request.getHeader(API_KEY_HEADER_NAME);

            // Validate the API key
            if (API_KEY_VALUE.equals(apiKey)) {
                // Proceed with the filter chain if valid
                filterChain.doFilter(request, response);
            } else {
                // Reject the request if invalid
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid API Key");
            }
        } else {
            // Proceed with other requests
            filterChain.doFilter(request, response);
        }
    }
}
