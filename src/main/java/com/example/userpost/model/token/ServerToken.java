package com.example.userpost.model.token;

import com.example.userpost.model.openid.Server;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "server_tokens")
@Getter
@Setter
public class ServerToken extends BaseToken {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "server_id", nullable = false)
  private Server server;

  public ServerToken(Server server, String refreshToken, Long refreshExpiredAt, String accessToken, Long accessExpiredAt) {
    super(refreshToken, refreshExpiredAt, accessToken, accessExpiredAt);
    this.server = server;
  }
}
