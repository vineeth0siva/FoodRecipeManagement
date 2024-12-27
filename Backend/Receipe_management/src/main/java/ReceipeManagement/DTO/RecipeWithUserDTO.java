package ReceipeManagement.DTO;

import java.time.LocalTime;

import ReceipeManagement.Entity.LEVELS;

public class RecipeWithUserDTO {
    private Long recipeId;
    private String recipeTitle;
    private String ingredients;
    private String instructions;
    private String imageUrl;
    private int likes;
    private LocalTime cookingTime;
    private String userName;
    private LEVELS difficultyLevel; // Enum for difficulty level
    private int viewCount;

    // Constructor, Getters, and Setters

    public RecipeWithUserDTO(Long recipeId, String recipeTitle, String ingredients, String instructions, 
                             String imageUrl, int likes, LocalTime cookingTime, String userName, 
                             LEVELS difficultyLevel, int viewCount) {
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.cookingTime = cookingTime;
        this.userName = userName;
        this.difficultyLevel = difficultyLevel;
        this.viewCount = viewCount;
    }

	public RecipeWithUserDTO() {
		super();
	}

	public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public String getRecipeTitle() {
		return recipeTitle;
	}

	public void setRecipeTitle(String recipeTitle) {
		this.recipeTitle = recipeTitle;
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

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public LocalTime getCookingTime() {
		return cookingTime;
	}

	public void setCookingTime(LocalTime cookingTime) {
		this.cookingTime = cookingTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LEVELS getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(LEVELS difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

  
}

