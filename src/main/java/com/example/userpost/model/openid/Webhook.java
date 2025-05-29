package com.example.userpost.model.openid;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "webhooks",
  indexes = {
    @Index(name = "idx_webhook_server_id", columnList = "connection_id")
  },
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"connection_id", "event_id"})
  }
)
@Getter
@Setter
@NoArgsConstructor
public class Webhook extends BaseSqlEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_id", nullable = false)
  private AcceptedConnection connection;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scope_id", nullable = false)
  private Scope scope;

  @Column(name = "scope_value", length = 50)
  private String scopeValue;

  @Column(name = "redirect_url", nullable = false)
  private String redirectUrl;

  public Webhook(AcceptedConnection connection, Event event, Scope scope, String redirectUrl) {
    super();
    this.connection = connection;
    this.event = event;
    this.scope = scope;
    this.redirectUrl = redirectUrl;
  }
}
