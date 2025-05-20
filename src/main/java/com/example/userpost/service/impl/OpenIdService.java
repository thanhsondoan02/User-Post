package com.example.userpost.service.impl;

import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.response.openid.connect.ServerInfoDto;
import com.example.userpost.model.openid.ConnectedServer;
import com.example.userpost.repository.ConnectedServerRepository;
import com.example.userpost.security.JwtUtils;
import com.example.userpost.service.IOpenIdService;
import com.example.userpost.util.PasswordUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OpenIdService implements IOpenIdService {

  private final ConnectedServerRepository connectedServerRepo;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final PasswordEncoder passwordEncoder;

  public OpenIdService(ConnectedServerRepository connectedServerRepo, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
    this.connectedServerRepo = connectedServerRepo;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public ServerInfoDto createConnect(ConnectRequestDto request) {
    var newServer = new ConnectedServer();
    newServer.setName(request.getName());
    newServer.setDomain(request.getDomain());
    newServer.setCallbackUrl(request.getCallbackUrl());

    var clientId = UUID.randomUUID().toString().replace("-", "");
    newServer.setClientId(clientId);

    var clientSecret = PasswordUtils.generateSecurePassword();
    newServer.setClientSecret(passwordEncoder.encode(clientSecret));

    var savedServer = connectedServerRepo.save(newServer);
    return new ServerInfoDto(savedServer, clientSecret);
  }
}
