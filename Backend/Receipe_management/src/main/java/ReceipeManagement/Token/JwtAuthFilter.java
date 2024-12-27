package ReceipeManagement.Token;

import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import ReceipeManagement.Token.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final UserDetailService userDetailService;

    public JwtAuthFilter(JwtService jwtService, UserDetailService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	logger.info("Enter into JWTAuthFilter page");
        // Skip the filter for the login page or specific public paths
        if (request.getRequestURI().equals("/login") || isPublished(request)) {
            filterChain.doFilter(request, response); // Allow request to pass without token validation
            return;
        }
    	logger.info("Enter into Second JWTAuthFilter page");
        try {
            
            final String authHeader = request.getHeader("Authorization");
            logger.info("Auth Header"+authHeader);
            String token;
            final String email;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);  // Extract token from the Authorization header
            }
            else {
                // Extract token from cookies if present
            	token = jwtService.getTokenFromCookies(request);
            	logger.info("Toke in else"+token);
            	if (token == null) {
            	    logger.warn("No token found in cookies for request: {}", request.getRequestURI());
            	} else {
            	    logger.info("Token found in cookies: {}", token);
            	}

            }
            logger.info("Extracted token: {}", token);

            // Extract email from the JWT token
            email = jwtService.ExtractEmail(token);
            if (email == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User email not found in token");
                return;
            }

            // Load user details from the UserDetailService (which implements UserDetailsService)
            UserDetails userDetails = userDetailService.loadUserByUsername(email);
            if (jwtService.isValidToken(token, userDetails)) {
                // If the token is valid, set up authentication in the SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User authenticated: {}", email);
            } else {
                logger.warn("Invalid token for user: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

        } catch (Exception e) {
            logger.error("Authentication failed", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error");
            return;
        }

        // Proceed with the filter chain after successful authentication
        filterChain.doFilter(request, response);
    }

    // Method to check if the request is for public paths (no authentication required)
    private boolean isPublished(HttpServletRequest req) {
        String uri = req.getRequestURI();
        // Allow requests to /user and /admin paths to bypass authentication
        return uri.startsWith("/user") ;
    }
}
