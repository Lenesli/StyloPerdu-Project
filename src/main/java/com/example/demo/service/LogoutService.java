package com.example.demo.service;


import com.example.demo.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override

    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");

        // Log to check if Authorization header is present
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);  // Extract the JWT token
        System.out.println("Extracted JWT Token: " + jwt);

        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        // Log to check if token is found
        if (storedToken != null) {
            System.out.println("Revoking token: " + storedToken.getToken());

            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);  // Save the updated token

            // Clear security context
            SecurityContextHolder.clearContext();

            System.out.println("Token revoked and context cleared.");
        } else {
            System.out.println("Token not found in database.");
        }
    }

}