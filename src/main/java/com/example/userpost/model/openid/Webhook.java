package com.example.userpost.model.openid;

import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.HookScope;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.util.convert.HookEventConverter;
import com.example.userpost.util.convert.HookScopeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "webhooks",
  indexes = {
    @Index(name = "idx_webhook_server_id", columnList = "server_id")
  },
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"server_id", "event"})
  }
)
@Getter
@Setter
@NoArgsConstructor
public class Webhook extends BaseSqlEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "server_id", nullable = false)
  private AcceptedConnection connection;

  @Column(name = "event", columnDefinition = "TINYINT", nullable = false)
  @Convert(converter = HookEventConverter.class)
  private HookEvent event;

  @Column(name = "scope", columnDefinition = "TINYINT", nullable = false)
  @Convert(converter = HookScopeConverter.class)
  private HookScope scope;

  @Column(name = "redirect_url", nullable = false)
  private String redirectUrl;

  public Webhook(AcceptedConnection connection, HookEvent event, HookScope scope, String redirectUrl) {
    super();
    this.connection = connection;
    this.event = event;
    this.scope = scope;
    this.redirectUrl = redirectUrl;
  }
}
