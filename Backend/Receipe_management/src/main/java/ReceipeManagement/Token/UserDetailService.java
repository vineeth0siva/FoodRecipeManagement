package ReceipeManagement.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ReceipeManagement.Repository.UserRepository;

@Service
public class UserDetailService implements UserDetailsService{
	private final UserRepository userRepository;
	
	public UserDetailService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	private static final Logger logger = LoggerFactory.getLogger(UserDetailService.class);
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		if (email == null || email.trim().isEmpty()) {
            logger.error("Attempted to load user with an empty or null email");
            throw new UsernameNotFoundException("Email is required to load user.");
        }

        logger.info("Attempting to load user by email: {}", email);

        // First try to find in user repository
        UserDetails user = userRepository.findByEmail(email).orElse(null);

        logger.info("User found with email: {}", email);
        logger.info("User found with user: {}", user);
        return user;
    }

}
