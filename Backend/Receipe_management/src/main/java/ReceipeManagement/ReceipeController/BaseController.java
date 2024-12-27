package ReceipeManagement.ReceipeController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import ReceipeManagement.Token.JwtService;
import ReceipeManagement.AdminService.GetAllUserDetails;
import ReceipeManagement.AdminService.RecipeService;
import ReceipeManagement.DTO.RecipeWithUserDTO;
import ReceipeManagement.DTO.UserRecipeDetailsDTO;
import ReceipeManagement.Entity.RecipeEntity;
import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Token.UserDetailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import ReceipeManagement.Repository.RecipeRepository;
import ReceipeManagement.Repository.UserRecipeRepository;
import ReceipeManagement.Repository.UserRepository;
import org.springframework.web.bind.annotation.CookieValue;

@Controller
public class BaseController {
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);


	@Autowired
	private UserDetailService userDetailService;

	@Autowired
	private JwtService jwtService;
	private RecipeRepository recipeRepository;
	@Autowired
	private UserRepository userRepository;
	private RecipeService  recipeService;
	private GetAllUserDetails getAllUserDetails;
	
	@Autowired
	private UserRecipeRepository userRecipeRepository;
	
	public BaseController(UserDetailService userDetailService, JwtService jwtService, UserRepository userRepository,
			RecipeService recipeService, UserRecipeRepository userRecipeRepository,RecipeRepository recipeRepository,
			GetAllUserDetails getAllUserDetails) {
		super();
		this.userDetailService = userDetailService;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.recipeService = recipeService;
		this.userRecipeRepository = userRecipeRepository;
		this.recipeRepository=recipeRepository;
		this.getAllUserDetails=getAllUserDetails;
	}

	@GetMapping("/login")
	public String loginPage() {
		return "Home"; // Render the login page
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, Model model, HttpServletResponse response) {
	    try {
	        // Authenticate the user
	        UserEntity user = (UserEntity) userDetailService.loadUserByUsername(email);
	        if (user != null && new BCryptPasswordEncoder().matches(password, user.getPassword())) {
	            // Generate JWT token
	            String token = jwtService.tokenGeneration(user);
	            user.setToken(token);
	            userRepository.save(user);

	            // Add the JWT token as a cookie to the response (you can use this cookie for subsequent requests)
	            Cookie cookie = new Cookie("Authorization",token);
	            cookie.setHttpOnly(true);  // Make it only accessible to the server
	            cookie.setSecure(true);    // Only send cookie over HTTPS
	            cookie.setPath("/");       // Accessible by all paths
	            cookie.setMaxAge(86400);    // Set expiration time (1 hour)
	            response.addCookie(cookie); // Add the cookie to the response

	            logger.info("Cookie set: " + cookie.getName() + "=" + cookie.getValue());

	            return "redirect:/admin/home"; // Redirect to the protected admin page
	        } else {
	            model.addAttribute("Error", "Invalid credentials");
	        }
	    } catch (Exception e) {
	        model.addAttribute("Error", e.getMessage());
	    }

	    return "Home"; // Return to the login page if something goes wrong
	}


	@GetMapping("/admin/home")
	public String homePage(Model model, @CookieValue(value = "Authorization", required = false) String token) {
	    logger.info("Home page requested");

	    if (token != null) {
	        try {
	            // Validate the token and extract email
	            String email = jwtService.ExtractEmail(token);
	            logger.info("Token is valid. Extracted email: {}", email);

	            if (email == null) {
	                logger.warn("Token extraction failed. Email not found.");
	                model.addAttribute("Error", "Invalid token");
	                return "login"; 
	            }

	            UserEntity user = (UserEntity) userDetailService.loadUserByUsername(email);
	            logger.info("User loaded from database: {}", user.getUsername());

	            model.addAttribute("user", user);

	            if (user.getRole().contains("ADMIN")) {
	                logger.info("User has admin role, redirecting to admin page.");
	                // Fetch all recipes with user details
	                List<RecipeWithUserDTO> recipes = userRecipeRepository.findAllRecipesWithUserDetails();
	                model.addAttribute("recipes", recipes);  // Pass list of recipes to the model
	                return "main";  // Redirect to admin home page
	            } else {
	                logger.info("User does not have admin role, redirecting to user page.");
	                return "Home";  // Redirect to user home page
	            }
	        } catch (Exception e) {
	            logger.error("Token validation failed", e);  // Log the error with exception
	            model.addAttribute("Error", "Invalid or expired token");
	            return "login";  // Redirect to login page if the token is invalid
	        }
	    } else {
	        logger.warn("No token found in the request.");
	        // If no token is found, redirect to the login page
	        return "redirect:/login";
	    }
	}
	
	@GetMapping("/admin/recipe/delete/{recipeId}")
	public String deleteRecipe(@PathVariable("recipeId") Long recipeId, Model model) {
	    logger.info("DELETE ENTERING");
	    try {
	        recipeService.deleteRecipeOnly(recipeId);

	        model.addAttribute("message", "Recipe and associated data deleted successfully!");
	        return "redirect:/admin/home";  // Redirect to a page after deletion
	    } catch (Exception e) {
	        logger.error("Error occurred during deletion", e);
	        model.addAttribute("error", "Failed to delete the recipe or associated data.");
	        return "main";  // Redirect to error page or show error message
	    }
	}
	@GetMapping("/admin/recipe/details/{id}")
	public String viewRecipeDetails(@PathVariable("id") Long id, Model model) {
	    // Fetch recipe data by ID
	    RecipeEntity recipe = recipeRepository.findById(id).orElse(null);

	    if (recipe != null) {
	        logger.info("Recipe found: " + recipe.toString());  // Log the recipe details
	    } else {
	        logger.error("Recipe with ID " + id + " not found.");
	    }

	    if (recipe != null) {
	        // Split instructions by newline character (\n)
	        String[] instructionsList = recipe.getInstructions().split("\n");  
	        
	        // Add the recipe and instructions to the model for rendering in the template
	        model.addAttribute("recipe", recipe);
	        model.addAttribute("instructions", instructionsList);  // Pass the split instructions list to the template
	        
	        return "Recipedetails";  // Return the name of the template to render
	    } else {
	        // Recipe not found, return an error page or a different view
	        model.addAttribute("error", "Recipe not found");
	        return "error";  // Return an error page if recipe is not found
	    }
	}


	@GetMapping("/admin/report")
	public String report(Model model) {
	    try {
	        // Fetch the list of recipes by likes
	        List<RecipeWithUserDTO> recipes = userRecipeRepository.findAllRecipeByLikes();
	        
	        // Check if the recipes list is empty or null
	        if (recipes == null || recipes.isEmpty()) {
	            model.addAttribute("error", "No recipes found.");
	            return "main";  // Redirect or show an error page if no recipes found
	        }

	        // Pass the recipes list to the model
	        model.addAttribute("recipes", recipes);
	        return "Report"; // Name of the view (template)
	    } catch (Exception e) {
	        // Log the error and pass the error message to the model
	        model.addAttribute("error", "Error fetching data: " + e.getMessage());
	        return "main"; // Return the error page or handle error
	    }
	}

	@GetMapping("/admin/user")
	public String getUser(Model model) {
	    try {
	        List<UserEntity> userEntities = userRepository.findAll();
	        
	        if (userEntities.isEmpty()) {
	            model.addAttribute("Error", "User data is empty");
	        } else {
	            model.addAttribute("user", userEntities);  // Add the user data to the model
	        }
	        
	        return "User";  // Returns the "User" view
	    } catch (Exception ex) {
	        model.addAttribute("error", "Error fetching data: " + ex.getMessage());
	        return "main";  // Redirect to the main page in case of error
	    }
	}

	 @GetMapping("admin/user/block/{userId}")
	    public String blockUserForOneDay(@PathVariable Long userId, Model model) {
	        try {
	        	recipeService.blockUserForOneDay(userId);  // Call service method to block the user
	            model.addAttribute("message", "User blocked for 1 day.");
	        } catch (Exception e) {
	            model.addAttribute("error", "Error blocking user: " + e.getMessage());
	        }
	        return "redirect:/admin/user";  // Redirect to user list after blocking
	    }
	 @GetMapping("admin/user/unblock/{userId}")
	    public String unblockUser(@PathVariable Long userId, Model model) {
	        try {
	            recipeService.unblockUser(userId);  // Call service method to unblock the user
	            model.addAttribute("message", "User unblocked successfully.");
	        } catch (Exception e) {
	            model.addAttribute("error", "Error unblocking user: " + e.getMessage());
	        }
	        return "redirect:/admin/user";  // Redirect to user list after unblocking
	    }
	 
	 @PostMapping("/admin/logout")
	 public String logout(HttpServletRequest request, HttpServletResponse response) {
	     // Invalidate the session
	     request.getSession().invalidate();

	     // Expire the "Authorization" cookie
	     Cookie cookie = new Cookie("Authorization", null);
	     cookie.setMaxAge(0);  // Set the cookie to expire immediately
	     cookie.setPath("/");  // Make the cookie valid for the entire domain
	     response.addCookie(cookie);
	     return "redirect:/login";
	 }
	 @GetMapping("/admin/userrecipedetails/{userId}")
	    public String getAllDetails(@PathVariable Long userId, Model model) {
	        try {
	            // Call the service method to get user details
	            UserRecipeDetailsDTO dto = getAllUserDetails.fullUserDetails(userId);
	            
	            // Add the details to the model
	            model.addAttribute("alldata", dto);
	            
	            // Return the view name
	            return "UserDetailspage";  // This will resolve to the "UserDetailspage" template
	        } 
	        catch (Exception e) {
	        	   Optional<UserEntity> userEntity = userRepository.findById(userId);
	               model.addAttribute("data", userEntity.orElse(null)); 
	            
	            
	            return "NoData";  
	        }
	    }
	 @GetMapping("/admin/deleteUserDetails/{userId}")
	 public String deleteUserDetails(@PathVariable Long userId, Model model) {
	     try {
	         // Call service method to delete user details
	    	 getAllUserDetails.deleteUserDetails(userId);

	         // Redirect to the user page or any other page after deletion
	         return "redirect:/admin/user"; // Redirecting to the user page
	     } catch (Exception e) {
	         model.addAttribute("error", "Error occurred while deleting user details.");
	         return "errorPage"; // Create an error page to display if something goes wrong
	     }
	 }
}
