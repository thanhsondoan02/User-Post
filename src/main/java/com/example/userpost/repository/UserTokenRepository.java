package com.example.userpost.repository;

import com.example.userpost.model.token.UserToken;
import com.example.userpost.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {
  @Query("SELECT u FROM UserToken t JOIN t.user u " +
    "WHERE t.accessToken = :token AND t.accessExpiredAt > :timeNow AND t.state = 1 AND u.state = 1")
  Optional<User> findUserByAccessToken(@Param("token") String token, @Param("timeNow") Long timeNow);

  @Query("SELECT t FROM UserToken t JOIN t.user u " +
    "WHERE t.refreshToken = :token AND t.refreshExpiredAt > :timeNow AND t.state = 1 AND u.state = 1")
  Optional<UserToken> findByRefreshToken(@Param("token") String token, @Param("timeNow") Long timeNow);
}
