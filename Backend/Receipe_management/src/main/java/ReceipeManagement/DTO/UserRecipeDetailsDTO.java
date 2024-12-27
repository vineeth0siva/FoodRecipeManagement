package ReceipeManagement.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import ReceipeManagement.Entity.LEVELS;

public class UserRecipeDetailsDTO {

	// User Information
	private Long userId;
	private String email;
	private String firstName;
	private String lastName;
	private Long phoneNumber;
	private String address;
	private String role;
	private String token;

	// List of Recipe Information
	private List<RecipeDetails> recipes;

	// Constructor
	public UserRecipeDetailsDTO(Long userId, String email, String firstName, String lastName, Long phoneNumber,
			String address, String role, String token, List<RecipeDetails> recipes) {
		this.userId = userId;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.role = role;
		this.token = token;
		this.recipes = recipes;
	}

	public UserRecipeDetailsDTO() {
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters for User Information
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long long1) {
		this.phoneNumber = long1;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	// Getters and Setters for Recipe List
	public List<RecipeDetails> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipeDetails> recipes) {
		this.recipes = recipes;
	}

	// Inner DTO Class for Recipe Details
	public static class RecipeDetails {
		private Long recipeId;
		private String title;
		private String ingredients;
		private String instructions;
		private String imageUrl;
		private String imagePublicId;
		private int likes;
		private LocalTime cookingTime;
		private LEVELS difficultyLevel;
		private int viewCount;
		private Long userRecipeId;
		private LocalDate generatedDate;
		private LocalTime generatedTime;

		// Constructor for RecipeDetails
		public RecipeDetails(Long recipeId, String title, String ingredients, String instructions, String imageUrl,
				String imagePublicId, int likes, LocalTime cookingTime, LEVELS difficultyLevel, int viewCount,
				Long userRecipeId, LocalDate generatedDate, LocalTime generatedTime) {
			this.recipeId = recipeId;
			this.title = title;
			this.ingredients = ingredients;
			this.instructions = instructions;
			this.imageUrl = imageUrl;
			this.imagePublicId = imagePublicId;
			this.likes = likes;
			this.cookingTime = cookingTime;
			this.difficultyLevel = difficultyLevel;
			this.viewCount = viewCount;
			this.userRecipeId = userRecipeId;
			this.generatedDate = generatedDate;
			this.generatedTime = generatedTime;
		}

		public RecipeDetails() {
			super();
		}

		// Getters and Setters for RecipeDetails
		public Long getRecipeId() {
			return recipeId;
		}

		public void setRecipeId(Long recipeId) {
			this.recipeId = recipeId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIngredients() {
			return ingredients;
		}

		public void setIngredients(String ingredients) {
			this.ingredients = ingredients;
		}

		public String getInstructions() {
			return instructions;
		}

		public void setInstructions(String instructions) {
			this.instructions = instructions;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getImagePublicId() {
			return imagePublicId;
		}

		public void setImagePublicId(String imagePublicId) {
			this.imagePublicId = imagePublicId;
		}

		public int getLikes() {
			return likes;
		}

		public void setLikes(int likes) {
			this.likes = likes;
		}

		public LocalTime getCookingTime() {
			return cookingTime;
		}

		public void setCookingTime(LocalTime localTime) {
			this.cookingTime = localTime;
		}

		public LEVELS getDifficultyLevel() {
			return difficultyLevel;
		}

		public void setDifficultyLevel(LEVELS levels) {
			this.difficultyLevel = levels;
		}

		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}

		public Long getUserRecipeId() {
			return userRecipeId;
		}

		public void setUserRecipeId(Long userRecipeId) {
			this.userRecipeId = userRecipeId;
		}

		public LocalDate getGeneratedDate() {
			return generatedDate;
		}

		public void setGeneratedDate(LocalDate localDate) {
			this.generatedDate = localDate;
		}

		public LocalTime getGeneratedTime() {
			return generatedTime;
		}

		public void setGeneratedTime(LocalTime localTime) {
			this.generatedTime = localTime;
		}
	}
}
