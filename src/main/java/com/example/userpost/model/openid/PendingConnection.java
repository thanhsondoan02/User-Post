package com.example.userpost.model.openid;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pending_connections")
@Getter
@Setter
@NoArgsConstructor
public class PendingConnection extends BaseSqlEntity {

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "domain", nullable = false)
  private String domain;

  @Column(name = "callback_url", nullable = false)
  private String callbackUrl;

  public PendingConnection(String name, String domain, String callbackUrl) {
    super();
    this.name = name;
    this.domain = domain;
    this.callbackUrl = callbackUrl;
  }
}
