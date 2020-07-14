package com.project.sauqi.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.sauqi.entity.User;

public interface UserRepo extends JpaRepository<User,Integer>
{
	public Optional<User> findByUsername(String username);
	public Optional<User> findByEmail(String email);	
	
	@Query(value = "SELECT * FROM user WHERE verify_token = ?1", nativeQuery = true)
	public Optional<User> findByVerif(String userVerif);
	
	
	@Query(value = "SELECT * FROM user WHERE id = ?1 and verify_token=?2", nativeQuery = true)
	public Optional<User> getResetPassword(int id, String verifytoken);
	
	@Query(value = "SELECT * FROM user WHERE id = ?1", nativeQuery = true)
	public Optional<User> getEmail(int id);
	
}
