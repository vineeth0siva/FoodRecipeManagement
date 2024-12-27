package ReceipeManagement.AdminService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ReceipeManagement.DTO.UserRecipeDetailsDTO;
import ReceipeManagement.Entity.RecipeEntity;
import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Entity.UserRecipe;
import ReceipeManagement.Repository.RecipeRepository;
import ReceipeManagement.Repository.UserRecipeRepository;
import ReceipeManagement.Repository.UserRepository;

@Service
public class GetAllUserDetails {
    private final UserRecipeRepository userRecipeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public GetAllUserDetails(UserRecipeRepository userRecipeRepository, RecipeRepository recipeRepository,
            UserRepository userRepository) {
        this.userRecipeRepository = userRecipeRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public UserRecipeDetailsDTO fullUserDetails(Long id) throws Exception {
        // Retrieve user from Optional and check if present
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        
        if (!optionalUser.isPresent()) {
            throw new Exception("User not found");
        }
        
        UserEntity user = optionalUser.get();
        
        // Retrieve user recipes
        List<UserRecipe> userRecipes = userRecipeRepository.findByUserId(id); // Make sure this is correct
        
        // Initialize the recipe entities list
        List<RecipeEntity> recipeEntities = new ArrayList<>();
        
        // Collect all recipes for the user
        for (UserRecipe userRecipe : userRecipes) {
            Optional<RecipeEntity> optionalRecipe = recipeRepository.findById(userRecipe.getRecipe().getId());

            // If recipe is present, add it to the list
            if (optionalRecipe.isPresent()) {
                recipeEntities.add(optionalRecipe.get());
            }
        }
        
        // If no recipes were found
        if (recipeEntities.isEmpty()) {
            throw new Exception("Recipes not found");
        }

        // Create the DTO and populate the data
        UserRecipeDetailsDTO userRecipeDetailsDTO = new UserRecipeDetailsDTO();
        
        // Set user details
        userRecipeDetailsDTO.setUserId(user.getId());
        userRecipeDetailsDTO.setFirstName(user.getFirstName());
        userRecipeDetailsDTO.setLastName(user.getLastName());
        userRecipeDetailsDTO.setAddress(user.getAddress());
        userRecipeDetailsDTO.setEmail(user.getEmail());
        userRecipeDetailsDTO.setPhoneNumber(user.getPhoneNumber());
        userRecipeDetailsDTO.setRole(user.getRole());  // Add role to the DTO (optional)
        userRecipeDetailsDTO.setToken(user.getToken());  // If needed, add token info

        // Map recipe entities to RecipeDetails DTOs
        List<UserRecipeDetailsDTO.RecipeDetails> recipeDetailsList = new ArrayList<>();
        for (RecipeEntity recipeEntity : recipeEntities) {
            // Find the UserRecipe corresponding to the recipe
            Optional<UserRecipe> userRecipeOptional = userRecipes.stream()
                    .filter(userRecipe -> userRecipe.getRecipe().getId().equals(recipeEntity.getId()))
                    .findFirst();

            if (userRecipeOptional.isPresent()) {
                UserRecipe userRecipe = userRecipeOptional.get();
                
                // Create a RecipeDetails DTO
                UserRecipeDetailsDTO.RecipeDetails recipeDetails = new UserRecipeDetailsDTO.RecipeDetails();
                recipeDetails.setRecipeId(recipeEntity.getId());
                recipeDetails.setTitle(recipeEntity.getTitle());
                recipeDetails.setIngredients(recipeEntity.getIngredients());
                recipeDetails.setInstructions(recipeEntity.getInstructions());
                recipeDetails.setImageUrl(recipeEntity.getImageUrl());
                recipeDetails.setImagePublicId(recipeEntity.getImagePublicId());
                recipeDetails.setLikes(recipeEntity.getLikes());
                recipeDetails.setCookingTime(recipeEntity.getCookingTime());
                recipeDetails.setDifficultyLevel(recipeEntity.getDifficultyLevel());
                recipeDetails.setViewCount(recipeEntity.getViewCount());
                recipeDetails.setUserRecipeId(userRecipe.getId());
                recipeDetails.setGeneratedDate(userRecipe.getGenerateddate());
                recipeDetails.setGeneratedTime(userRecipe.getGeneratedTime());

                // Add to the list
                recipeDetailsList.add(recipeDetails);
            }
        }

        // Set the recipes in the DTO
        userRecipeDetailsDTO.setRecipes(recipeDetailsList);

        return userRecipeDetailsDTO;
    }
    public void deleteUserDetails(Long userId) throws Exception {
        // Delete user recipes associated with the user
        List<UserRecipe> userRecipes = userRecipeRepository.findByUserId(userId);
        for (UserRecipe userRecipe : userRecipes) {
            // You can also choose to delete the recipes if needed
            // recipeRepository.delete(userRecipe.getRecipe()); // Uncomment if you want to delete the recipes as well
            userRecipeRepository.delete(userRecipe);
        }

        // Delete user entity
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isPresent()) {
            userRepository.delete(userEntity.get());
        } else {
            throw new Exception("User not found");
        }
    }

}
