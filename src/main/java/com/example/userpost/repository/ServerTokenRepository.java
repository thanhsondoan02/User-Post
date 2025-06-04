package com.example.userpost.repository;

import com.example.userpost.model.openid.Server;
import com.example.userpost.model.token.ServerToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ServerTokenRepository extends JpaRepository<ServerToken, String> {
  @Query("SELECT s FROM ServerToken t JOIN t.server s JOIN t.connection c " +
    "WHERE t.accessToken = :token AND t.accessExpiredAt > :timeNow AND t.state = 1 " +
    "AND s.state = 1 AND c.status = 1 AND c.state = 1")
  Optional<Server> findServerByAccessToken(@Param("token") String token, @Param("timeNow") Long timeNow);

  @Query("SELECT t FROM ServerToken t JOIN t.server s JOIN t.connection c " +
    "WHERE t.refreshToken = :token AND t.refreshExpiredAt > :timeNow AND t.state = 1 " +
    "AND s.state = 1 AND c.status = 1 AND c.state = 1")
  Optional<ServerToken> findByRefreshToken(@Param("token") String token, @Param("timeNow") Long timeNow);

  @Modifying
  @Transactional
  @Query("UPDATE ServerToken t SET t.state = 0 WHERE t.connection.id = :connectionId AND t.state = 1")
  void deleteTokensByConnectionId(@Param("connectionId") String id);
}
