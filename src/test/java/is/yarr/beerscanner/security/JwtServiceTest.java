package is.yarr.beerscanner.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify that the Keys.secretKeyFor method generates a secure key
 * for the HMAC-SHA256 algorithm.
 */
public class JwtServiceTest {

    @Test
    public void testSecureKeyGeneration() {
        // Generate a secure key for HS256 algorithm
        Key secureKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Create claims for the token
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test@example.com");
        claims.put("name", "Test User");

        // Generate a token with the secure key
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 day
                .signWith(secureKey, SignatureAlgorithm.HS256)
                .compact();

        // Print debug info
        System.out.println("[DEBUG_LOG] Generated token: " + token);

        // Token should not be null or empty
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Parse the token and verify claims
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(secureKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Subject should match the email
        assertEquals("test@example.com", parsedClaims.getSubject());
        assertEquals("Test User", parsedClaims.get("name"));
    }
}
