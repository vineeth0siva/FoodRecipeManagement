package ReceipeManagement.SecurityConfiguration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import ReceipeManagement.Token.JwtAuthFilter;
import ReceipeManagement.Token.UserDetailService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer  {
	private final JwtAuthFilter jwtAuthFilter;
	private final UserDetailService userDetailService;
	public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailService userDetailService) {
		super();
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailService = userDetailService;
	}
	@Bean
	public SecurityFilterChain security(HttpSecurity http) throws Exception {
    	http.csrf(c -> c.disable())
        .authorizeHttpRequests(request -> request
        	.requestMatchers("/user/register", "/user/login","/login","/error").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/recipeenter/**").hasRole("USER")
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Stateless authentication (JWT)
        .authenticationProvider(authenticationProvider()) // Your custom authentication provider, if any
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Use the Filter class here
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                    .logoutUrl("/Recipe/logout") // Custom logout URL
                    .logoutSuccessUrl("/Recipe/logout?logout=true")
                    .permitAll()) 
    		.cors().configurationSource(corsConfigurationSource());
        return http.build();
    }

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authprovider = new DaoAuthenticationProvider();
		authprovider.setUserDetailsService(userDetailService); // Set the correct UserDetailsService
		authprovider.setPasswordEncoder(passwordEncoder());
		return authprovider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	@Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "******",
            "api_key", "*******",
            "api_secret", "**************C60"
        ));
    }
	 @Override
	 public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**") // Allow all endpoints
	            .allowedOrigins("http://localhost:3000") // Allow requests from React (adjust if using different port)
	            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow necessary methods
	            .allowedHeaders("*") // Allow any headers
	            .allowCredentials(true); // Allow credentials (cookies, etc.)
	    }
	 @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.addAllowedOrigin("http://localhost:3000"); // React front-end origin
	        configuration.addAllowedMethod("OPTIONS"); // Allow preflight (OPTIONS) requests
	        configuration.addAllowedMethod("GET"); // Allow GET requests
	        configuration.addAllowedMethod("POST"); // Allow POST requests
	        configuration.addAllowedMethod("DELETE");
	        configuration.addAllowedMethod("PUT"); // Allow PUT requests
	        configuration.addAllowedHeader("*"); // Allow all headers (including Authorization)
	        configuration.setAllowCredentials(true); // Allow credentials (cookies, etc.)

	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration); // Apply CORS to all endpoints
	        return source;
	    }
}
