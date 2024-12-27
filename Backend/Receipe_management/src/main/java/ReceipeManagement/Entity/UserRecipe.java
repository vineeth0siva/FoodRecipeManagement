package ReceipeManagement.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserRecipe")
public class UserRecipe {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
	private UserEntity user;
	@ManyToOne
	@JoinColumn(name = "recipe_id",referencedColumnName = "id",nullable = false)
	private RecipeEntity recipe;
	@Column(name = "generated_date", nullable = false)
	private LocalDate generateddate;
	 @Column(name = "generated_time", nullable = false)
	private LocalTime generatedTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UserEntity getUser() {
		return user;
	}
	public void setUser(UserEntity user) {
		this.user = user;
	}
	public RecipeEntity getRecipe() {
		return recipe;
	}
	public void setRecipe(RecipeEntity recipe) {
		this.recipe = recipe;
	}
	public LocalDate getGenerateddate() {
		return generateddate;
	}
	public void setGenerateddate(LocalDate localDate) {
		this.generateddate = localDate;
	}
	public LocalTime getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(LocalTime generatedTime) {
		this.generatedTime = generatedTime;
	}
	
}
