package com.example.userpost.repository;

import com.example.userpost.constant.HookScope;
import com.example.userpost.model.openid.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScopeRepository extends JpaRepository<Scope, String> {
  @Query("SELECT e FROM Scope e WHERE e.type =:type AND e.state = 1")
  Optional<Scope> findActiveByType(@Param("type") HookScope type);

  @Query("""
    SELECT DISTINCT s FROM EventScope es JOIN es.event e JOIN es.scope s
    WHERE e.state = 1 AND es.state = 1 AND s.state = 1
    """)
  List<Scope> getActiveScopes();
}
