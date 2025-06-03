package com.example.userpost.repository;

import com.example.userpost.model.openid.Server;
import com.example.userpost.model.token.ServerToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServerTokenRepository extends JpaRepository<ServerToken, String> {
  @Query("SELECT s FROM ServerToken t JOIN t.server s " +
    "WHERE t.accessToken = :token AND t.accessExpiredAt > :timeNow AND t.state = 1 AND s.state = 1")
  Optional<Server> findServerByAccessToken(@Param("token") String token, @Param("timeNow") Long timeNow);

  @Query("SELECT t FROM ServerToken t JOIN t.server s " +
    "WHERE t.refreshToken = :token AND t.refreshExpiredAt > :timeNow AND t.state = 1 AND s.state = 1")
  Optional<ServerToken> findByRefreshToken(@Param("token") String token, @Param("timeNow") Long timeNow);
}
