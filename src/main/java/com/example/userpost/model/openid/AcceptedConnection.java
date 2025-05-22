package com.example.userpost.model.openid;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accepted_connections")
@Getter
@Setter
@NoArgsConstructor
public class AcceptedConnection extends BaseConnection {

  @Column(name = "client_id", length = 32, nullable = false, unique = true)
  private String clientId;

  @Column(name = "client_secret", nullable = false)
  private String clientSecret;

  @OneToMany(mappedBy = "connection", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Webhook> webhooks = new ArrayList<>();

  public AcceptedConnection(BaseConnection connection) {
    super();
    this.name = connection.getName();
    this.domain = connection.getDomain();
    this.callbackUrl = connection.getCallbackUrl();
  }
}
