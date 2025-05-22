package com.example.userpost.dto.response.openid.connect;

import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.State;
import com.example.userpost.model.openid.AcceptedConnection;
import com.example.userpost.model.openid.BaseConnection;
import com.example.userpost.model.openid.PendingConnection;
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

  public ConnectionDto(BaseConnection connection) {
    this.id = connection.getId();
    this.name = connection.getName();
    this.domain = connection.getDomain();
    this.callbackUrl = connection.getCallbackUrl();
    this.createdAt = connection.getCreatedAt();

    if (connection instanceof AcceptedConnection accepted) {
      this.clientId = accepted.getClientId();
      this.status = ConnectionStatus.ACCEPTED.toString();
    }

    if (connection instanceof PendingConnection pending) {
      switch (pending.getState()) {
        case ACTIVE -> {
          this.status = ConnectionStatus.PENDING.toString();
        }
        case INACTIVE -> {
          this.status = ConnectionStatus.REJECTED.toString();
        }
      }
    }
  }
}

