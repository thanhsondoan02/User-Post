package com.example.userpost.repository;

import com.example.userpost.constant.State;
import com.example.userpost.model.openid.AcceptedConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AcceptedConnectionRepository extends JpaRepository<AcceptedConnection, String> {

  List<AcceptedConnection> findAllByState(State state);

  @Query("SELECT v FROM AcceptedConnection v WHERE v.clientId = :clientId AND v.state = 1")
  Optional<AcceptedConnection> findActiveByClientId(@Param("clientId") String clientId);


  @Query("SELECT v FROM AcceptedConnection v WHERE v.id = :id AND v.state = 1")
  Optional<AcceptedConnection> findActiveById(@Param("id") String id);

  boolean existsByDomainAndState(String domain, State state);
}
