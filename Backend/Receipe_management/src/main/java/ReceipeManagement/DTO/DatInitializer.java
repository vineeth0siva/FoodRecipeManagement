package ReceipeManagement.DTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Repository.UserRepository;

import java.util.List;

@Component
public class DatInitializer implements CommandLineRunner {

    private final UserRepository user;
    private final PasswordEncoder password;

    @Autowired
    public DatInitializer(UserRepository user, PasswordEncoder password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user exists
        List<UserEntity> existingUsers = user.findByRole("ADMIN"); // Use "ADMIN" without ROLE_ prefix
        if (existingUsers.isEmpty()) {
            // Create a new admin user
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail("aparna2@gmail.com");
            userEntity.setPassword(password.encode("aparna")); // Use passwordEncoder to encode password
            userEntity.setRole("ADMIN"); // Set role without "ROLE_" prefix
            userEntity.setFirstName("aparna");
            userEntity.setLastName("V A");
            userEntity.setAdmin(true);
            
            // Save the new user
            user.save(userEntity);
            System.out.println("Admin user created successfully.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
