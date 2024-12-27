package ReceipeManagement.ReceipeController;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Repository.UserRepository;
import ReceipeManagement.Token.JwtAuthFilter;
import ReceipeManagement.Token.JwtService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path = "/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtservice;

	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,JwtService jwtservice) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager=authenticationManager;
		this.jwtservice=jwtservice;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserEntity user) {
		logger.error("Registration attempt - Email: {}", user.getEmail());
		logger.error("Full User Object: {}", user.toString());
		try {
			if (user.getEmail() == null || user.getPassword() == null) {
				return ResponseEntity.badRequest().body("Email and password are required");
			}

			// Check if email already exists
			if (userRepository.findByEmail(user.getEmail()).isPresent()) {
				return ResponseEntity.badRequest().body("Email already in use");
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setRole("USER");
			userRepository.save(user);
		}
		catch (Exception e) {
	        logger.error("Registration error", e);
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	            .body("Authentication failed: " + e.getMessage());
	    }

		return ResponseEntity.ok("User registered successfully");
	}
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserEntity user){
	    try {
	        authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

	        // Fetch the user from the database to get the correct role
	        UserEntity u = userRepository.findByEmail(user.getEmail())
	                                     .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        if (u.isBlocked()) {
	            return ResponseEntity.ok("User can't login now");
	        }

	        logger.info("User role before generating token: {}", u.getRole());

	        // Use the 'u' object (with the role) for token generation
	        String token = jwtservice.tokenGeneration(u);
	        u.setToken(token);
	        userRepository.save(u);
	        return ResponseEntity.ok(Map.of("token", token));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("User authorization Error");
	    }
	}
}
