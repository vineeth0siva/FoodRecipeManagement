package ReceipeManagement.AdminService;

import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ReceipeManagement.DTO.RecipeWithUserDTO;
import ReceipeManagement.Entity.RecipeEntity;
import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Entity.UserRecipe;
import ReceipeManagement.ReceipeController.BaseController;
import ReceipeManagement.Repository.RecipeRepository;
import ReceipeManagement.Repository.UserRecipeRepository;
import ReceipeManagement.Repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class RecipeService {
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRecipeRepository userRecipeRepository;
    

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository,
			UserRecipeRepository userRecipeRepository) {
		super();
		this.recipeRepository = recipeRepository;
		this.userRepository = userRepository;
		this.userRecipeRepository = userRecipeRepository;
	}

    @Transactional
    public void deleteRecipeOnly(Long recipeId) {
        try {
            logger.info("Deleting recipe and its associated user_recipe records");

            // Fetch user_recipe records for the given recipeId
            List<UserRecipe> userRecipes = userRecipeRepository.findByRecipeIdUsingQuery(recipeId);

            // Ensure there are associated user_recipe records
            if (userRecipes != null && !userRecipes.isEmpty()) {
                // Delete the user_recipe associations for the given recipe
                userRecipeRepository.deleteAllByRecipeId(recipeId);  // Assuming this method exists in your repository
                logger.info("Deleted user_recipe associations for recipe with ID {}", recipeId);
            }

            // Finally, delete the recipe itself from the recipe table
            recipeRepository.deleteById(recipeId);
            logger.info("Recipe with ID {} deleted", recipeId);

        } catch (Exception e) {
            logger.error("Error occurred during recipe deletion: ", e);
            throw new RuntimeException("Failed to delete the recipe and related associations: " + e.getMessage(), e);
        }
    }
    public void blockUserForOneDay(Long userId) {
        Optional<UserEntity> userEntityOpt = userRepository.findById(userId);
        
        if (userEntityOpt.isPresent()) {
            UserEntity userEntity = userEntityOpt.get();
            
            // Set user as blocked
            userEntity.setBlocked(true);
            
            // Set block expiration time (current time + 1 day)
            LocalDateTime blockExpirationTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS);
            userEntity.setBlockedUntil(blockExpirationTime);
            
            // Save the updated user entity
            userRepository.save(userEntity);
            
            System.out.println("User blocked for 1 day.");
        } else {
            System.out.println("User not found.");
        }
    }

    // Method to check if the user is blocked
    public boolean isUserBlocked(Long userId) {
        Optional<UserEntity> userEntityOpt = userRepository.findById(userId);
        
        if (userEntityOpt.isPresent()) {
            UserEntity userEntity = userEntityOpt.get();
            
            if (userEntity.isBlocked() && userEntity.getBlockedUntil().isAfter(LocalDateTime.now())) {
                // The user is still blocked
                return true;
            } else {
                // The block has expired or the user is not blocked
                userEntity.setBlocked(false);  // Unblock the user
                userEntity.setBlockedUntil(null);  // Remove the block expiration timestamp
                userRepository.save(userEntity);  // Save updated user entity
                return false;
            }
        }
        return false;  // User not found
    }
    public void unblockUser(Long userId) {
        Optional<UserEntity> userEntityOpt = userRepository.findById(userId);
        
        if (userEntityOpt.isPresent()) {
            UserEntity userEntity = userEntityOpt.get();
            
            // Set user as unblocked
            userEntity.setBlocked(false);
            userEntity.setBlockedUntil(null);  // Reset blockedUntil field
            
            // Save the updated user entity
            userRepository.save(userEntity);
        } else {
            throw new RuntimeException("User not found");
        }
    }

}