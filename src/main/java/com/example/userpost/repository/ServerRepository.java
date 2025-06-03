package com.example.userpost.repository;

import com.example.userpost.model.openid.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, String> {
  @Query("SELECT COUNT(c) > 0 FROM Server c WHERE c.domain = :domain AND c.state = 1")
  boolean existsByDomain(@Param("domain") String domain);

  @Query("SELECT COUNT(c) > 0 FROM Server c WHERE c.owner = 1 AND c.state = 1")
  boolean existsOwnerServer();

  @Query("SELECT c FROM Server c WHERE c.owner = 1 AND c.state = 1")
  Optional<Server> findOwnerServer();

  @Query("SELECT s FROM Server s JOIN Connection c " +
    "WHERE s.domain = :domain AND s.owner = 0 AND s.state = 1" +
    "AND (c.status = 0 or c.status = 1) AND c.state = 1")
  boolean isConnectionWithServerExists(@Param("domain") String domain);
}
