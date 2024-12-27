package ReceipeManagement.ReceipeController;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.constant.Constable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ReceipeManagement.DTO.RecipeWithUserDTO;
import ReceipeManagement.Entity.LEVELS;
import ReceipeManagement.Entity.RecipeEntity;
import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Entity.UserRecipe;
import ReceipeManagement.Repository.RecipeRepository;
import ReceipeManagement.Repository.UserRecipeRepository;
import ReceipeManagement.Repository.UserRepository;
import ReceipeManagement.Token.ImageUploadService;
import ReceipeManagement.Token.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path = "/recipeenter")
public class UserController1 {
	private static int incrementcontrol = 0;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private final Cloudinary cloudinary;
	private final UserRepository userRepository;
	private final RecipeRepository recipeRepository;
	private final UserRecipeRepository userRecipeRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtservice;
	@Autowired
	private ImageUploadService imageUploadService;

	public UserController1(UserRepository userRepository, RecipeRepository recipeRepository,
			UserRecipeRepository userRecipeRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtservice, Cloudinary cloudinary) {
		super();
		this.userRepository = userRepository;
		this.recipeRepository = recipeRepository;
		this.userRecipeRepository = userRecipeRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtservice = jwtservice;
		this.cloudinary = cloudinary;
	}

	@PostMapping("/details")
	public ResponseEntity<?> userdetails(@RequestHeader(value = "Authorization", required = true) String tokenvalue,
			@RequestBody UserEntity user) {

		// IMPORTANT: Proper token handling
		if (tokenvalue == null || !tokenvalue.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
		}

		// Remove "Bearer " prefix
		String token = tokenvalue.substring(7);

		try {
			// Extensive validation checks
			if (jwtservice.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
			}

			// Extract critical information
			String role = jwtservice.extractRole(token);
			Long userId = jwtservice.extractUserId(token);
			String email = jwtservice.ExtractEmail(token);

			// Logging for debugging
			System.out.println("Token Role: " + role);
			System.out.println("User ID: " + userId);
			System.out.println("Email: " + email);

			// Additional role-based access control
			if (!"USER".equals(role)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient privileges->USER");
			}

			// Existing logic for user details update
			UserEntity u = userRepository.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			// Update logic
			if (user.getPassword() != null) {
				u.setPassword(passwordEncoder.encode(u.getPassword()));
			}
			if (user.getAddress() != null) {
				u.setAddress(user.getAddress());
			}
			if (user.getPhoneNumber() != null) {
				u.setPhoneNumber(user.getPhoneNumber());
			}

			userRepository.save(u);

			return ResponseEntity.ok("User details updated successfully");

		} catch (Exception e) {
			// Detailed error logging
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		}

	}

	@PostMapping("/addrecipe")
	@Transactional
	public ResponseEntity<?> addRecipe(@RequestHeader(value = "Authorization", required = true) String tokenValue,
			@RequestParam("title") String title, @RequestParam("ingredients") String ingredients,
			@RequestParam("instructions") String instructions, @RequestParam("image") MultipartFile image,
			@RequestParam("difficultyLevel") String difficultyLevel, // Capture difficulty level
			@RequestParam("cookingTime") String cookingTime) { // Capture cooking time

		try {
			// Token validation
			tokenValue = tokenValue.trim();
			if (tokenValue.startsWith("Bearer ")) {
				tokenValue = tokenValue.substring(7);
			}

			// Extract user ID from JWT token
			Long userId = jwtservice.extractUserId(tokenValue);
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found.");
			}

			// Validate recipe data
			if (title == null || title.isEmpty() || ingredients == null || ingredients.isEmpty()
					|| difficultyLevel == null || difficultyLevel.isEmpty()) {
				return ResponseEntity.badRequest().body("Recipe details not completed");
			}

			// Find the user by ID
			UserEntity user = userRepository.findById(userId)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			// Upload the image to Cloudinary and get the URL
			String imageUrl = imageUploadService.uploadImage(image);

			// Convert the difficultyLevel string to the LEVELS enum
			LEVELS level = LEVELS.valueOf(difficultyLevel.toUpperCase()); // Convert to enum

			// Parse cooking time (assuming it's in HH:MM:SS format)
			LocalTime time = LocalTime.parse(cookingTime);

			// Save the recipe to the database
			RecipeEntity recipe = new RecipeEntity();
			recipe.setTitle(title);
			recipe.setIngredients(ingredients);
			recipe.setInstructions(instructions);
			recipe.setImageUrl(imageUrl); // Save the image URL
			recipe.setLikes(0);
			recipe.setDifficultyLevel(level); // Set the difficulty level
			recipe.setCookingTime(time); // Set the cooking time
			RecipeEntity savedRecipe = recipeRepository.save(recipe);

			UserRecipe userRecipe = new UserRecipe();
			userRecipe.setUser(user);
			userRecipe.setRecipe(savedRecipe);
			userRecipe.setGenerateddate(LocalDate.now());
			userRecipe.setGeneratedTime(LocalTime.now());
			userRecipeRepository.save(userRecipe);

			return ResponseEntity.ok("Recipe Added Successfully");

		} catch (IOException e) {
			// Log the error for debugging
			logger.error("Error adding recipe", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			// This is for invalid difficultyLevel values
			return ResponseEntity.badRequest().body("Invalid difficulty level.");
		}
	}

	@PutMapping("/updaterecipe/{id}")
	@Transactional
	public ResponseEntity<?> updateRecipe(@RequestHeader(value = "Authorization", required = true) String tokenValue,
			@PathVariable Long id, // Recipe ID
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "ingredients", required = false) String ingredients,
			@RequestParam(value = "instructions", required = false) String instructions,
			@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "difficultyLevel", required = false) String difficultyLevel,
			@RequestParam(value = "cookingTime", required = false) String cookingTime) {
		try {
			// Token validation
			tokenValue = tokenValue.trim();
			if (tokenValue.startsWith("Bearer ")) {
				tokenValue = tokenValue.substring(7);
			}

			// Extract user ID from JWT token
			Long userId = jwtservice.extractUserId(tokenValue);
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found.");
			}
			System.out.println(title);
			// Check if the user has this recipe (ownership check)
			UserRecipe userRecipe = userRecipeRepository.findByUserIdAndRecipeId(userId, id)
					.orElseThrow(() -> new RuntimeException("You are not authorized to update this recipe."));
			userRecipe.setGenerateddate(LocalDate.now());
			userRecipe.setGeneratedTime(LocalTime.now());
			userRecipeRepository.save(userRecipe); // Save updated userRecipe

			// Fetch the recipe from the Recipe table
			RecipeEntity existingRecipe = recipeRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

			// Update recipe fields only if provided in the request
			if (title != null && !title.isEmpty()) {
				existingRecipe.setTitle(title);
			}
			if (ingredients != null && !ingredients.isEmpty()) {
				existingRecipe.setIngredients(ingredients);
			}
			if (instructions != null && !instructions.isEmpty()) {
				existingRecipe.setInstructions(instructions);
			}
			if (image != null && !image.isEmpty()) {
				// Upload the new image to Cloudinary (or your image upload service)
				String imageUrl = imageUploadService.uploadImage(image);
				existingRecipe.setImageUrl(imageUrl); // Save the new image URL
			}
			if (difficultyLevel != null && !difficultyLevel.isEmpty()) {
				// Convert the difficultyLevel string to the LEVELS enum
				try {
					LEVELS level = LEVELS.valueOf(difficultyLevel.toUpperCase()); // Convert to enum
					existingRecipe.setDifficultyLevel(level); // Set the new difficulty level
				} catch (IllegalArgumentException e) {
					return ResponseEntity.badRequest().body("Invalid difficulty level.");
				}
			}
			if (cookingTime != null && !cookingTime.isEmpty()) {
				// Parse cooking time (assuming it's in HH:MM:SS format)
				try {
					LocalTime time = LocalTime.parse(cookingTime);
					existingRecipe.setCookingTime(time); // Set the new cooking time
				} catch (DateTimeParseException e) {
					return ResponseEntity.badRequest().body("Invalid cooking time format.");
				}
			}

			// Save the updated recipe to the database
			recipeRepository.save(existingRecipe);

			return ResponseEntity.ok("Recipe updated successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		}
	}

	@DeleteMapping("/deleterecipe/{id}")
	public ResponseEntity<?> deleteRecipe(@RequestHeader(value = "Authorization", required = true) String tokenValue,
			@PathVariable Long id) {
		try {
			// Trim whitespace or newline characters from the token
			tokenValue = tokenValue.trim(); // Remove leading/trailing spaces/newlines

			// Check if token starts with "Bearer" and extract the token value
			if (tokenValue.startsWith("Bearer ")) {
				tokenValue = tokenValue.substring(7); // Remove 'Bearer ' prefix
			}

			// Extract user ID from JWT token
			Long userId;
			try {
				userId = jwtservice.extractUserId(tokenValue);
			} catch (ExpiredJwtException e) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token has expired.");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
			}

			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found in token.");
			}

			// Check if the user owns the recipe in the UserRecipe table
			UserRecipe userRecipe = userRecipeRepository.findByUserIdAndRecipeId(userId, id)
					.orElseThrow(() -> new RuntimeException("You are not authorized to delete this recipe."));

			// Fetch the recipe from the Recipe table
			RecipeEntity recipe = recipeRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

			// If the recipe has an associated image, delete it from Cloudinary
			if (recipe.getImagePublicId() != null) {
				try {
					cloudinary.uploader().destroy(recipe.getImagePublicId(), ObjectUtils.emptyMap());
				} catch (Exception e) {
					logger.warn("Failed to delete image from Cloudinary", e);
				}
			}

			// Delete the UserRecipe entry to disassociate the user and the recipe
			userRecipeRepository.delete(userRecipe);

			// Delete the recipe from the Recipe table
			recipeRepository.delete(recipe); // Delete the recipe

			return ResponseEntity.ok("Recipe deleted successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		}
	}

	@GetMapping("/allrecipes")
	public ResponseEntity<List<RecipeWithUserDTO>> getAllRecipesWithUser() {
		try {
			// Fetch the list of all recipes with associated user details
			List<RecipeWithUserDTO> recipeWithUserDTOs = userRecipeRepository.findAllRecipesWithUserDetails();

			if (recipeWithUserDTOs.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if no data is found
			}

			return ResponseEntity.ok(recipeWithUserDTOs); // Return the data with 200 OK status
		} catch (Exception e) {
			logger.error("Error fetching recipes with user details", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 if an error occurs
		}
	}

	@GetMapping("/recipe/{id}")
	public ResponseEntity<?> getRecipeById(@PathVariable Long id,
			@RequestHeader(value = "Authorization", required = true) String tokenValue) {
		try {
			// Trim and clean token
			tokenValue = tokenValue.trim();
			if (tokenValue.startsWith("Bearer ")) {
				tokenValue = tokenValue.substring(7); // Remove 'Bearer ' prefix
			}

			// Extract userId from token
			Long userId = jwtservice.extractUserId(tokenValue);

			// Fetch RecipeEntity and validate
			RecipeEntity recipe = recipeRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + id));

			// Fetch UserRecipe details by recipe ID
			UserRecipe userRecipe = userRecipeRepository.findByRecipeId(id);
			if (userRecipe == null) {
				throw new RuntimeException("UserRecipe not found for recipe ID: " + id);
			}

			// Fetch UserEntity and validate
			UserEntity user = userRepository.findById(userRecipe.getUser().getId())
					.orElseThrow(() -> new RuntimeException("User not found for ID: " + userRecipe.getUser().getId()));

			logger.info("User details from UserRecipe: ID={} Name={}", userRecipe.getUser().getId(),
					userRecipe.getUser().getFirstName());
			logger.info("User ID from token: {}", userId);

			// Increment view count only if the logged-in user is not the creator of the
			// recipe
			if (!userRecipe.getUser().getId().equals(userId)) {
				incrementcontrol += 1;
				if (incrementcontrol == 2) {
					logger.info("Incrementing view count for recipe ID: {}", id);
					recipe.setViewCount(recipe.getViewCount() + 1); // Increment by 1
					recipeRepository.save(recipe);
					incrementcontrol = 0;
				}
				// Save the updated view count
			} else {
				logger.info("User is the recipe owner, view count not incremented.");
			}

			// Combine user's first and last name
			String name = user.getFirstName() + " " + user.getLastName();

			// Create and populate DTO
			RecipeWithUserDTO dto = new RecipeWithUserDTO(recipe.getId(), recipe.getTitle(), recipe.getIngredients(),
					recipe.getInstructions(), recipe.getImageUrl(), recipe.getLikes(), recipe.getCookingTime(), name,
					recipe.getDifficultyLevel(), recipe.getViewCount());

			return ResponseEntity.ok(dto);

		} catch (Exception e) {
			logger.error("Error fetching recipe with ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/myrecipe")
	public ResponseEntity<?> getMyRecipes(@RequestHeader(value = "Authorization", required = true) String tokenvalue) {
		try {
			tokenvalue = tokenvalue.trim(); // Remove leading/trailing spaces/newlines
			if (tokenvalue.startsWith("Bearer ")) {
				tokenvalue = tokenvalue.substring(7); // Remove 'Bearer ' prefix
			}

			// Validate JWT token
			Long userId = jwtservice.extractUserId(tokenvalue);
			if (userId == null) {
				// Return unauthorized status if token is invalid
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
			}

			// Fetch all recipe IDs associated with the user from the userrecipe table
			List<UserRecipe> userRecipes = userRecipeRepository.findByUserId(userId);

			if (userRecipes.isEmpty()) {
				// If no recipes are found, return a NOT_FOUND response with a message
				return ResponseEntity.status(HttpStatus.OK).body("No recipes found for this user.");
			}

			// Extract all recipe IDs from the userRecipe list
			List<Long> recipeIds = userRecipes.stream().map(userRecipe -> userRecipe.getRecipe().getId())
					.collect(Collectors.toList());

			// Fetch the recipes
			List<RecipeEntity> recipes = recipeRepository.findAllById(recipeIds);

			// Ensure the recipes are in the same order as the recipeIds list
			Map<Long, RecipeEntity> recipeMap = recipes.stream()
					.collect(Collectors.toMap(RecipeEntity::getId, recipe -> recipe));

			List<RecipeEntity> orderedRecipes = recipeIds.stream().map(recipeMap::get).collect(Collectors.toList());

			// Return the ordered recipes
			return ResponseEntity.ok(orderedRecipes);

		} catch (Exception e) {
			e.printStackTrace();
			// Return an error response in case of any exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
	}

	@PutMapping("/{id}/happy")
	public ResponseEntity<?> addLike(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		try {
			// Check if token is valid and extract the userId
			if (token.startsWith("Bearer ")) {
				token = token.substring(7); // Remove 'Bearer ' prefix
			}
			Long userId = jwtservice.extractUserId(token);

			// Fetch the recipe by its id
			RecipeEntity recipe = recipeRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

			// Fetch the user who created the recipe
			UserRecipe userre = userRecipeRepository.findByRecipeId(id);
			Long recipeCreatorId = userre.getUser().getId();
			// Check if the logged-in user is the same as the creator of the recipe
			if (userId.equals(recipeCreatorId)) {
				return ResponseEntity.status(400).body("You cannot like your own recipe.");
			}

			// If the logged-in user is not the creator, increment the likes
			recipe.incrementLikes();
			recipeRepository.save(recipe);

			// Return the updated likes count
			return ResponseEntity.ok(recipe.getLikes());

		} catch (Exception e) {
			// Handle errors and provide feedback
			return ResponseEntity.status(500).body("Error adding like: " + e.getMessage());
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getRecipesWithUsers(@RequestParam(required = false) String search) {
		List<RecipeEntity> recipes;

		if (search == null || search.trim().isEmpty()) {
			// Fetch all recipes if search query is empty or null
			recipes = recipeRepository.findAll();
		} else {
			// Fetch recipes matching the search query (case-insensitive)
			recipes = recipeRepository.findByTitleContainingIgnoreCase(search);
		}

		// If no recipes are found, return a 404 status
		if (recipes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching data found.");
		}

		// For each recipe, find associated UserRecipe and User details, then map to DTO
		List<RecipeWithUserDTO> recipeWithUserDTOs = recipes.stream().map(recipe -> {
			// Find the user associated with the recipe
			UserRecipe userRecipe = userRecipeRepository.findByRecipeId(recipe.getId());
			if (userRecipe == null) {
				return null; // Skip if no user associated with the recipe
			}

			UserEntity user = userRecipe.getUser();

			// Create DTO for RecipeWithUser
			RecipeWithUserDTO dto = new RecipeWithUserDTO();
			dto.setRecipeId(recipe.getId());
			dto.setRecipeTitle(recipe.getTitle());
			dto.setIngredients(recipe.getIngredients());
			dto.setInstructions(recipe.getInstructions());
			dto.setImageUrl(recipe.getImageUrl());
			dto.setLikes(recipe.getLikes());
			dto.setCookingTime(recipe.getCookingTime());
			dto.setUserName(user != null ? user.getFirstName() : null); // Set user name
			dto.setDifficultyLevel(recipe.getDifficultyLevel());
			dto.setViewCount(recipe.getViewCount());

			return dto;
		}).filter(Objects::nonNull) // Filter out null values (if any)
				.collect(Collectors.toList());

		// Return the list of DTOs
		return ResponseEntity.ok(recipeWithUserDTOs);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = true) String token) {
		try {
			if (token == null || !token.startsWith("Bearer ")) {
				return ResponseEntity.badRequest()
						.body("Invalid token format. Token must be in the format: Bearer <token>");
			}

			// Remove the "Bearer " prefix from the token to extract the actual JWT
			token = token.substring(7);

			// Extract email from the JWT token
			String userEmail = jwtservice.ExtractEmail(token);
			System.out.println("Token extracted for logout: " + userEmail);

			if (userEmail == null || userEmail.isEmpty()) {
				return ResponseEntity.badRequest().body("Invalid token. Could not extract user email.");
			}

			// Fetch the user from the database using the extracted email
			UserEntity u = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			// Remove the token from the user entity (log out)
			u.setToken(null);
			userRepository.save(u); // Save the updated user (without token)

			// Log the successful logout
			logger.info("User {} has logged out successfully.", userEmail);

			// Respond with a success message
			return ResponseEntity.ok("User logged out successfully.");
		} catch (UsernameNotFoundException e) {
			logger.error("User not found during logout: ", e);
			return ResponseEntity.badRequest().body("User not found.");
		} catch (JwtException e) {
			logger.error("Error extracting email from token: ", e);
			return ResponseEntity.badRequest().body("Invalid or expired token.");
		} catch (Exception e) {
			logger.error("Error during logout: ", e);
			return ResponseEntity.badRequest().body("Error during logout.");
		}
	}

	@GetMapping("/navigation/{id}")
    public ResponseEntity<?> getNavigation(@RequestHeader(value = "Authorization", required = true) String token,
                                           @PathVariable Long id) {

        try {
            UserRecipe userRecipe = userRecipeRepository.findByRecipeId(id);
            if (userRecipe == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User recipe not found for ID: " + id);
            }

            int views = userRecipe.getRecipe().getViewCount();
            LocalDate date = userRecipe.getGenerateddate();

            return ResponseEntity.ok("Views: " + views + ", Generated Date: " + date);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
