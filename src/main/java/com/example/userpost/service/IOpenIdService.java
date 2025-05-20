package com.example.userpost.service;

import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.response.openid.connect.ServerInfoDto;

public interface IOpenIdService {
  ServerInfoDto createConnect(ConnectRequestDto request);
}
