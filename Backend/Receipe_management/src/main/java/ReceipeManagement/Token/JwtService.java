package ReceipeManagement.Token;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ReceipeManagement.Entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
	private static final String SECRET_KEY="anjjcjcdcnvfdnnnnjedjwjdoikncncnksnckjdnncnnsnhbhnnjcjkdcjjd";
	//Token Generation
	public String tokenGeneration(UserEntity user) {
	    logger.info("Generating token for user: {}, Role: {}", user.getEmail(), user.getRole());
	    if (user.getRole() == null) {
	        logger.warn("Role is null for user: {}", user.getEmail());
	    }
	    if(user.getRole()=="ADMIN") {
	    	user.setRole("ROLE_ADMIN");
	    }
	    Date issuedAt = new Date(System.currentTimeMillis());
	    Date expirationTime = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);  // 24 hours expiration

	    return Jwts.builder()
	        .subject(user.getEmail())
	        .claim("userId", user.getId())
	        .claim("role", user.getRole())
	        .issuedAt(issuedAt)
	        .expiration(expirationTime)
	        .signWith(getSigninKey())
	        .compact();
	}
	
	private SecretKey getSigninKey() {
		byte[] key=Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(key);
	}
	//Token Username Extraction
	private Claims ExtractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token).getPayload();
	}
	private <T> T extractClaims(String token,java.util.function.Function<Claims, T> claimResolver) {
		final Claims claims=ExtractAllClaims(token);
		return claimResolver.apply(claims);
	}
	public String ExtractEmail(String token) {
		return extractClaims(token, Claims::getSubject);
	}
	 public Long extractUserId(String token) {
	        return extractClaims(token, claims -> claims.get("userId", Long.class));  // Extract userId (custom claim)
	    }
	public boolean isValidToken(String token, UserDetails user) {
	    final String email = ExtractEmail(token);
	    return email.equals(user.getUsername()) && !isTokenExpired(token);
	}
	
	public boolean isTokenExpired(String token) {
	    Date expirationDate = extractClaims(token, Claims::getExpiration);
	    Date currentDate = new Date();

	    logger.info("Token expiration date: {}", expirationDate);
	    logger.info("Current date: {}", currentDate);

	    return expirationDate.before(currentDate);
	}
	
	public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));  // Extract role (custom claim)
    }
	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
	    try {
	        Claims claims = ExtractAllClaims(token);
	        String email = ExtractEmail(token);
	        String role = claims.get("role", String.class);  // Extract the role from the JWT

	        logger.info("Authentication details - Email: {}, Role: {}", email, role);

	        // Ensure that roles are prefixed with "ROLE_"
	        List<GrantedAuthority> authorities = new ArrayList<>();
	        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));  // Adding "ROLE_" prefix

	        return new UsernamePasswordAuthenticationToken(email, null, authorities);
	    } catch (Exception e) {
	        logger.error("Error creating authentication token", e);
	        throw e;
	    }
	}
	public String getTokenFromCookies(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("Authorization".equals(cookie.getName())) {
	                return cookie.getValue(); // The token value
	            }
	        }
	    }
	    return null; // Return null if the token is not found
	}
	
}
