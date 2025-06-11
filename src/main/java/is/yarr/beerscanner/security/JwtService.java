package is.yarr.beerscanner.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import is.yarr.beerscanner.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT token generation and validation.
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private Key signingKey;

    /**
     * Extract username from JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a claim from JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver the function to extract the claim
     * @param <T> the type of the claim
     * @return the claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate a JWT token for a user.
     *
     * @param user the user
     * @return the JWT token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        return generateToken(claims, user.getEmail());
    }

    /**
     * Generate a JWT token with claims.
     *
     * @param extraClaims the extra claims
     * @param subject the subject (username)
     * @return the JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate a JWT token.
     *
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check if a JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date from JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract all claims from JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get signing key for JWT token.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        if (signingKey == null) {
            if (jwtSecret == null || jwtSecret.equals("defaultSecretKey")) {
                // Generate a secure key for HS256 algorithm only once
                signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            } else {
                // Use the provided secret key if it's not the default
                byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
                signingKey = Keys.hmacShaKeyFor(keyBytes);
            }
        }
        return signingKey;
    }
}