package com.example.userpost.dto.response.openid.connect;

import com.example.userpost.model.openid.Connection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConnectionListResponseDto {
  private List<ConnectionDto> connections;

  public ConnectionListResponseDto(List<Connection> connections) {
    this.connections = connections.stream()
      .map(ConnectionDto::new)
      .toList();
  }
}

