package ReceipeManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ReceipeManagement.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{

	Optional<UserEntity>findByEmail(String email);

	UserEntity findById(UserEntity user);
	
	List<UserEntity> findByRole(String string);
}
