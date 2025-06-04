package com.example.userpost.repository;

import com.example.userpost.model.openid.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WebhookRepository extends JpaRepository<Webhook, String> {
  @Query("SELECT w FROM Webhook w WHERE w.id = ?1 AND w.state = 1")
  Optional<Webhook> findActiveById(String id);

  @Query("SELECT w FROM Webhook w WHERE w.connection.id = ?1 AND w.state = 1")
  List<Webhook> findByConnectionId(String connectionId);

  @Query("""
    SELECT w FROM Webhook w
    WHERE w.eventScope.event = :event
    AND w.eventScope.scope = :scope
    AND w.state = 1
    """)
  List<Webhook> findActiveByScopeEvent(@Param("scope") Scope scope, @Param("event") Event event);

  @Modifying
  @Transactional
  @Query("UPDATE Webhook w SET w.state = 0 WHERE w.connection.id = :connectionId AND w.state = 1")
  void deleteWebhooksByConnectionId(@Param("connectionId") String id);
}
