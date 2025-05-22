package com.example.userpost.service;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.response.openid.connect.ConnectionDto;
import com.example.userpost.dto.response.openid.connect.ConnectionListResponseDto;

public interface IOpenIdService {
  void addPendingConnections(ConnectRequestDto request);
  boolean isConnectionExistAndPending(String id);
  ConnectionDto updateConnection(String id, ConnectionAction action);
  ConnectionListResponseDto getConnections(ConnectionStatus status);
}
