package com.example.userpost.repository;

import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.HookScope;
import com.example.userpost.model.openid.Event;
import com.example.userpost.model.openid.EventScope;
import com.example.userpost.model.openid.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, String> {
  @Query("SELECT e FROM Event e WHERE e.type =:type AND e.state = 1")
  Optional<Event> findActiveByType(@Param("type") HookEvent type);

  @Query("SELECT es FROM EventScope es " +
    "JOIN es.event e " +
    "JOIN es.scope s " +
    "WHERE e.type = :eventType AND s.type = :scopeType " +
    "AND es.state = 1 AND e.state = 1 AND s.state = 1")
  Optional<EventScope> findActiveEventScope(
    @Param("eventType") HookEvent eventType,
    @Param("scopeType") HookScope eventScope
  );
}
