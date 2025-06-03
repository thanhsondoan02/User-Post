package com.example.userpost.dto.response.openid.connect;

import com.example.userpost.model.openid.Connection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionDto {
  private String id;
  private String name;
  private String domain;
  private String callbackUrl;
  private String clientId;
  private String clientSecret;
  private Long createdAt;
  private String status;

  public ConnectionDto(Connection connection) {
    this.id = connection.getId();
    this.name = connection.getTargetServer().getName();
    this.domain = connection.getTargetServer().getDomain();
    this.callbackUrl = connection.getCallbackUrl();
    this.createdAt = connection.getCreatedAt();
    this.status = connection.getStatus().toString();
    this.clientId = connection.getClientId();
  }
}

