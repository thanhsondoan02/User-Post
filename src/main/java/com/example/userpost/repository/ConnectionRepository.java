package com.example.userpost.repository;

import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.model.openid.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, String> {

  @Query("SELECT v FROM Connection v WHERE v.id = :id AND v.state = 1")
  Optional<Connection> findActiveById(@Param("id") String id);

  @Query("SELECT v FROM Connection v WHERE v.status = :status AND v.state = 1")
  List<Connection> findActiveByStatus(@Param("status") ConnectionStatus status);

  @Query("SELECT c FROM Connection c " +
    "WHERE c.clientId = :clientId AND c.state = 1" +
    "AND c.targetServer.state = 1")
  Optional<Connection> findActiveByClientId(@Param("clientId") String clientId);

  @Query("SELECT v FROM Connection v WHERE v.id = :id AND v.status = :status AND v.state = 1")
  Optional<Connection> findActiveByIdAndStatus(@Param("id") String id, @Param("status") ConnectionStatus status);
}
