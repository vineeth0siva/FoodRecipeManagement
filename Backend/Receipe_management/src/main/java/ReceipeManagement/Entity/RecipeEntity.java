package ReceipeManagement.Entity;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "recipe")
public class RecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Lob
    private String ingredients;

    @Lob
    private String instructions;

    private String imageUrl;

    @Column(name = "image_public_id")
    private String imagePublicId;
    
    private int likes;

    @Column(name = "cooking_time")
    private LocalTime cookingTime;

    private LEVELS difficultyLevel;
    @Column(name = "view_count")
    private int viewCount = 0;
    
    
    

	public RecipeEntity(Long id, String title, String ingredients, String instructions, String image,
			String imagePublicId, int likes, LocalTime cookingTime, LEVELS difficultyLevel) {
		super();
		this.id = id;
		this.title = title;
		this.ingredients = ingredients;
		this.instructions = instructions;
		this.imageUrl = image;
		this.imagePublicId = imagePublicId;
		this.likes = likes;
		this.cookingTime = cookingTime;
		this.difficultyLevel = difficultyLevel;
	}

	public RecipeEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LEVELS getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(LEVELS difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public void incrementLikes() {
		this.likes=this.likes+1;
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

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
