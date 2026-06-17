package com.example.api_gateway.controller;

import com.example.api_gateway.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller for API Gateway
 * Handles JWT token generation and validation
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @Value("${security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${security.jwt.expiration-time:86400000}")
    private long jwtExpiration;

    /**
     * Login endpoint - Generate JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return Mono.fromCallable(() -> {
            // Validate credentials (hardcoded for demo - replace with database lookup)
            if (isValidUser(loginRequest.getUsername(), loginRequest.getPassword())) {
                String token = generateToken(loginRequest.getUsername());
                
                AuthResponse response = new AuthResponse();
                response.setToken(token);
                response.setType("Bearer");
                response.setUsername(loginRequest.getUsername());
                response.setExpiresIn(jwtExpiration);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                error.put("message", "Username or password is incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        });
    }

    /**
     * Validate token endpoint
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<?>> validateToken(@RequestHeader("Authorization") String authHeader) {
        return Mono.fromCallable(() -> {
            try {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String username = jwtService.extractUsername(token);
                    
                    if (jwtService.isTokenValid(token, username)) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("valid", true);
                        response.put("username", username);
                        response.put("message", "Token is valid");
                        return ResponseEntity.ok(response);
                    }
                }
                
                Map<String, String> error = new HashMap<>();
                error.put("valid", "false");
                error.put("error", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("valid", "false");
                error.put("error", "Token validation failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        });
    }

    /**
     * Test protected endpoint
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public Mono<ResponseEntity<Map<String, String>>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "✅ JWT Security is working! You are authenticated.");
        response.put("service", "api-gateway");
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Get current user info from token
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public Mono<ResponseEntity<?>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        return Mono.fromCallable(() -> {
            try {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String username = jwtService.extractUsername(token);
                    
                    if (jwtService.isTokenValid(token, username)) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("username", username);
                        response.put("service", "api-gateway");
                        response.put("authenticated", true);
                        return ResponseEntity.ok(response);
                    }
                }
                
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or missing token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to get user info: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        });
    }

    /**
     * Logout endpoint (token invalidation would happen client-side)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("info", "Please remove the token from client storage");
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Refresh token endpoint
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<?>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        return Mono.fromCallable(() -> {
            try {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String oldToken = authHeader.substring(7);
                    String username = jwtService.extractUsername(oldToken);
                    
                    if (jwtService.isTokenValid(oldToken, username)) {
                        String newToken = generateToken(username);
                        
                        AuthResponse response = new AuthResponse();
                        response.setToken(newToken);
                        response.setType("Bearer");
                        response.setUsername(username);
                        response.setExpiresIn(jwtExpiration);
                        
                        return ResponseEntity.ok(response);
                    }
                }
                
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token refresh failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        });
    }

    // Helper methods

    private boolean isValidUser(String username, String password) {
        // Hardcoded users for demo - replace with database lookup or external auth service
        Map<String, String> users = new HashMap<>();
        users.put("admin", "admin123");
        users.put("user", "user123");
        users.put("test", "test123");
        users.put("demo", "demo123");
        
        return users.containsKey(username) && users.get(username).equals(password);
    }

    private String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", username.equals("admin") ? "ADMIN" : "USER");
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

// DTOs

@Data
@NoArgsConstructor
@AllArgsConstructor
class LoginRequest {
    private String username;
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AuthResponse {
    private String token;
    private String type;
    private String username;
    private Long expiresIn;
}
