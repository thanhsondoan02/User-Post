package com.example.userpost.service;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.response.openid.connect.ServerInfoDto;

public interface IOpenIdService {
  void addPendingConnections(ConnectRequestDto request);
  boolean isConnectionExistAndPending(String id);
  ServerInfoDto updateConnection(String id, ConnectionAction action);
}
