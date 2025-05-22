package com.example.userpost.dto.response.openid.connect;

import com.example.userpost.model.openid.AcceptedConnection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerInfoDto {
  private String id;
  private String name;
  private String domain;
  private String callbackUrl;
  private String clientId;
  private String clientSecret;
  private Long createdAt;

  public ServerInfoDto(AcceptedConnection entity, String clientSecret) {
    this.id = entity.getId();
    this.name = entity.getName();
    this.domain = entity.getDomain();
    this.callbackUrl = entity.getCallbackUrl();
    this.clientId = entity.getClientId();
    this.clientSecret = clientSecret;
    this.createdAt = entity.getCreatedAt();
  }
}

