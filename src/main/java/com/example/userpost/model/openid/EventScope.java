package com.example.userpost.model.openid;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_scopes")
@Getter
@Setter
@NoArgsConstructor
public class EventScope extends BaseSqlEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scope_id", nullable = false)
  private Scope scope;

  public EventScope(Event event, Scope scope) {
    super();
    this.event = event;
    this.scope = scope;
  }
}
