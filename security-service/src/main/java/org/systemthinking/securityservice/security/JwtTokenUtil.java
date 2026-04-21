
//20-3-26

//⚡ WHY we need this?
//
//        Without JWT:
//
//        Every request → go to DB → check user → slow ❌
//
//        With JWT:
//
//        User logs in once → gets token
//
//        Every request → just verify token → fast ✅



package org.systemthinking.securityservice.security;
// WHAT: This defines the package (folder structure)
// WHY: Helps organize code properly

import io.jsonwebtoken.*;
// WHAT: JWT library classes
// WHY: Used to create, parse, and validate JWT tokens

import io.jsonwebtoken.security.Keys;
// WHAT: Utility to create secure keys
// WHY: Needed to sign JWT securely

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// WHAT: Logging tool
// WHY: Helps print errors/logs (important for debugging)

import org.springframework.beans.factory.annotation.Value;
// WHAT: Used to read values from application.properties
// WHY: To avoid hardcoding secrets in code

import org.springframework.security.core.userdetails.UserDetails;
// WHAT: Spring Security user object
// WHY: Contains username, password, roles

import org.springframework.stereotype.Component;
// WHAT: Marks this class as Spring Bean
// WHY: Spring will automatically create and manage it

import javax.crypto.SecretKey;
// WHAT: Represents secret key used in encryption
// WHY: Used to sign JWT

import java.nio.charset.StandardCharsets;
// WHAT: Used for encoding string → bytes
// WHY: Required for creating secure key

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
// WHAT: Utility classes
// WHY: Used for storing claims and extracting values

@Component
// WHAT: Makes this class available to Spring (auto-injection)
// WHY: So other classes can use it easily
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    // WHAT: Logger instance
    // WHY: Used to log errors and debug messages

    @Value("${jwt.secret}")
    private String secret;
    // WHAT: Secret key loaded from config
    // WHY: Used to sign and verify JWT

    @Value("${jwt.expiration}")
    private Long expiration;
    // WHAT: Token expiration time
    // WHY: Token should not live forever (security)

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    // WHAT: Refresh token expiry
    // WHY: Refresh tokens usually live longer

// ================= SIGNING KEY =================

    private SecretKey getSigningKey(){
        // WHAT: Convert secret string to bytes
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // WHAT: Create secure HMAC SHA key
        return Keys.hmacShaKeyFor(keyBytes);
        // WHY: Needed to sign and verify JWT
    }

// ================= EXTRACT USERNAME =================

    public String getUsernameFromToken(String token){
        // WHAT: Extract "subject" (username) from token
        return getClaimsFromToken(token, Claims::getSubject);
        // WHY: Username is stored inside token
    }

// ================= EXTRACT EXPIRATION =================

    public Date getExpirationDateFromToken(String token){
        // WHAT: Extract expiration date
        return getClaimsFromToken(token, Claims::getExpiration);
        // WHY: Used to check if token is expired
    }

// ================= GENERIC CLAIM EXTRACTOR =================

    public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver){
        // WHAT: Get all claims from token
        final Claims claims = getAllClaimsFromToken(token);

        // WHAT: Apply function to extract required data
        return claimsResolver.apply(claims);
        // WHY: Flexible way to extract anything
    }

// ================= PARSE TOKEN =================

    private Claims getAllClaimsFromToken(String token){
        try{
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    // WHAT: Verify signature
                    // WHY: Ensures token is not tampered

                    .build()

                    .parseClaimsJws(token)
                    // WHAT: Parse token
                    // WHY: Decode JWT

                    .getBody();
            // WHAT: Get payload (claims)
        }

        catch(ExpiredJwtException e){
            logger.error("JWT TOKEN IS EXPIRED{}", e.getMessage());
            throw e;
        }

        catch(UnsupportedJwtException e){
            logger.error("JWT TOKEN IS UNSUPPORTED{}", e.getMessage());
            throw e;
        }

        catch(MalformedJwtException e){
            logger.error("INVALID JWT TOKEN {}", e.getMessage());
            throw e;
        }

        catch(SecurityException e){
            logger.error("INVALID JWT Signature {}", e.getMessage());
            throw e;
        }

        catch (IllegalArgumentException e){
            logger.error("JWT CLAIMS STRING IS EMPTY {}", e.getMessage());
            throw e;
        }
    }

// ================= CHECK EXPIRY =================

    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);

        // WHAT: Check if token time is before current time
        return expiration.before(new Date());

        // WHY: If expired → reject request
    }

// ================= GENERATE ACCESS TOKEN =================

    public String generateToken(UserDetails userDetails){

        Map<String, Object> claims = new HashMap<>();
        // WHAT: Create claims (extra data)

        claims.put("roles", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList());
        // WHAT: Store user roles
        // WHY: Needed for authorization

        return createToken(claims, userDetails.getUsername(), expiration);
        // WHAT: Create token
    }

// ================= GENERATE REFRESH TOKEN =================

    public String generateRefreshToken(UserDetails userDetails){

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList());

        return createToken(claims, userDetails.getUsername(), refreshExpiration);
        // WHY: Refresh token lives longer
    }

// ================= CORE TOKEN CREATION =================

    private String createToken(Map<String, Object> claims, String subject, Long expirationMillis){

        Date now = new Date();
        // WHAT: Current time

        Date expirationDate = new Date(now.getTime() + expirationMillis);
        // WHAT: Expiry time
        // WHY: Set token validity duration

        return Jwts.builder()

                .claims(claims)
                // WHAT: Add custom data

                .subject(subject)
                // WHAT: Username

                .issuedAt(now)
                // WHAT: Token creation time

                .expiration(expirationDate)
                // WHAT: Expiry time

                .signWith(getSigningKey())
                // WHAT: Sign token
                // WHY: Prevent tampering

                .compact();
        // WHAT: Convert to string token
    }

// ================= VALIDATE TOKEN =================

    public Boolean validateToken(String token, UserDetails userDetails){
        try{
            final String username = getUsernameFromToken(token);

            return (username.equals(userDetails.getUsername())
                    && !isTokenExpired(token));

            // WHY:
            // 1. Username must match
            // 2. Token must not be expired

        }catch (JwtException | IllegalArgumentException e){
            logger.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

// ================= VALIDATE REFRESH TOKEN =================

    public Boolean validateRefreshToken(String token){
        try{
            getAllClaimsFromToken(token);
            // WHAT: Just parsing ensures validity

            return !isTokenExpired(token);

        }catch (JwtException | IllegalArgumentException e){
            logger.error("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }
}