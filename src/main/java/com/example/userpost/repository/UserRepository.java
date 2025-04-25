package com.example.userpost.repository;

import com.example.userpost.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  List<User> findByUsernameIgnoreCaseContainingOrFullNameIgnoreCaseContaining(String username, String fullName);
}
