package com.example.userpost.repository;

import com.example.userpost.model.openid.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, String> {
}
