package com.example.userpost.repository;

import com.example.userpost.constant.State;
import com.example.userpost.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUsernameAndState(String username, State state);

  Optional<User> findByEmailAndState(String email, State state);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  @Query("""
    SELECT u FROM User u WHERE u.state = 1 AND
    (LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
  List<User> searchByKeyword(@Param("keyword") String keyword);

  @Query("SELECT u FROM User u WHERE u.state = 1")
  List<User> getActiveUsers();

  @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id AND u.state = 1")
  boolean existsAndActiveById(@Param("id") String id);

  @Query("SELECT v FROM User v WHERE v.username = :username AND v.state = 1")
  Optional<User> findActiveByUserName(@Param("username") String username);
}
