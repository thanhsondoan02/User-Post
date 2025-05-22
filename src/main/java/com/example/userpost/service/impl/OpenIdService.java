package com.example.userpost.service.impl;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.constant.State;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.response.openid.connect.ServerInfoDto;
import com.example.userpost.model.openid.AcceptedConnection;
import com.example.userpost.model.openid.PendingConnection;
import com.example.userpost.repository.AcceptedConnectionRepository;
import com.example.userpost.repository.PendingConnectionRepository;
import com.example.userpost.security.JwtUtils;
import com.example.userpost.service.IOpenIdService;
import com.example.userpost.util.PasswordUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OpenIdService implements IOpenIdService {

  private final AcceptedConnectionRepository acceptedConnectionRepo;
  private final PendingConnectionRepository pendingConnectionRepo;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final PasswordEncoder passwordEncoder;

  public OpenIdService(
    AcceptedConnectionRepository acceptedConnectionRepo,
    PendingConnectionRepository pendingConnectionRepo,
    AuthenticationManager authenticationManager,
    JwtUtils jwtUtils,
    PasswordEncoder passwordEncoder
  ) {
    this.acceptedConnectionRepo = acceptedConnectionRepo;
    this.pendingConnectionRepo = pendingConnectionRepo;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void addPendingConnections(ConnectRequestDto request) {
    var newConnection = new PendingConnection(
      request.getName(),
      request.getDomain(),
      request.getCallbackUrl()
    );

    pendingConnectionRepo.save(newConnection);
  }

  @Override
  public boolean isConnectionExistAndPending(String id) {
    return pendingConnectionRepo.existsAndActiveById(id);
  }

  @Override
  public ServerInfoDto updateConnection(String id, ConnectionAction action) {
    switch (action) {
      case ACCEPT -> {
        var pendingConnection = pendingConnectionRepo.findByIdAndState(id, State.ACTIVE)
          .orElseThrow(() -> new RuntimeException("Group not found"));

        var newConnection = new AcceptedConnection(pendingConnection);

        //  Generate clientId and clientSecret
        var clientId = UUID.randomUUID().toString().replace("-", "");
        newConnection.setClientId(clientId);

        var clientSecret = PasswordUtils.generateSecurePassword();
        newConnection.setClientSecret(passwordEncoder.encode(clientSecret));

        // Save new connection and delete pending connection
        var saved = acceptedConnectionRepo.save(newConnection);
        pendingConnectionRepo.deleteById(pendingConnection.getId());

        return new ServerInfoDto(saved, clientSecret);
      }
      case REJECT -> {
        pendingConnectionRepo.updateState(id, State.INACTIVE);
        return null;
      }
      default -> throw new IllegalArgumentException("Invalid action: " + action);
    }
  }
}
