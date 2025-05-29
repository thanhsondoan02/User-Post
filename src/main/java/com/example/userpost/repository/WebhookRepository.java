package com.example.userpost.repository;

import com.example.userpost.model.openid.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WebhookRepository extends JpaRepository<Webhook, String> {
  @Query("SELECT w FROM Webhook w WHERE w.connection.id = ?1")
  List<Webhook> findByConnectionId(String connectionId);
}
