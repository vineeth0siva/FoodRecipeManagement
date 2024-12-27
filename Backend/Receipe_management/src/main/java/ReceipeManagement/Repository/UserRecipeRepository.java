package ReceipeManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ReceipeManagement.DTO.RecipeWithUserDTO;
import ReceipeManagement.Entity.UserEntity;
import ReceipeManagement.Entity.UserRecipe;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {

	Optional<UserRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);

	@Query("SELECT ur FROM UserRecipe ur WHERE ur.user.id = :userId ORDER BY ur.generateddate DESC, ur.generatedTime DESC")
	List<UserRecipe> findByUserId(@Param("userId") Long userId);

	@Query("SELECT new ReceipeManagement.DTO.RecipeWithUserDTO(r.id, r.title, r.ingredients, r.instructions, r.imageUrl, r.likes, r.cookingTime, u.firstName, r.difficultyLevel, r.viewCount) "
			+ "FROM UserRecipe ur " + "JOIN ur.recipe r " + "JOIN ur.user u "
			+ "ORDER BY ur.generateddate DESC, ur.generatedTime DESC")
	List<RecipeWithUserDTO> findAllRecipesWithUserDetails();

	@Query("SELECT new ReceipeManagement.DTO.RecipeWithUserDTO(r.id, r.title, r.ingredients, r.instructions, r.imageUrl, r.likes, r.cookingTime, u.firstName, r.difficultyLevel, r.viewCount) "
			+ "FROM UserRecipe ur " + "JOIN ur.recipe r " + "JOIN ur.user u " + "ORDER BY r.likes DESC")
	List<RecipeWithUserDTO> findAllRecipeByLikes();

	@Query("SELECT ur FROM UserRecipe ur WHERE ur.recipe.id = :id")
	UserRecipe findByRecipeId(@Param("id") Long id);

	@Query("SELECT ur FROM UserRecipe ur WHERE ur.recipe.id = :id")
	List<UserRecipe> findByRecipeIdUsingQuery(@Param("id") Long id);

	public void deleteById(Long recipeId);

	void deleteAllByRecipeId(Long recipeId);


}
