package ReceipeManagement.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ReceipeManagement.Entity.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long>{
	List<RecipeEntity> findByTitleContainingIgnoreCase(String title);
}
