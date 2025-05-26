package com.example.userpost.service.impl;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.State;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.response.openid.connect.ConnectionDto;
import com.example.userpost.dto.response.openid.connect.ConnectionListResponseDto;
import com.example.userpost.model.openid.AcceptedConnection;
import com.example.userpost.model.openid.BaseConnection;
import com.example.userpost.model.openid.PendingConnection;
import com.example.userpost.repository.AcceptedConnectionRepository;
import com.example.userpost.repository.PendingConnectionRepository;
import com.example.userpost.security.JwtUtils;
import com.example.userpost.service.IOpenIdService;
import com.example.userpost.util.PasswordUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
  public ConnectionDto addPendingConnections(ConnectRequestDto request) {
    var newConnection = new PendingConnection(
      request.getName(),
      request.getDomain(),
      request.getCallbackUrl()
    );

    return new ConnectionDto(pendingConnectionRepo.save(newConnection));
  }

  @Override
  public boolean isConnectionExistAndPending(String id) {
    return pendingConnectionRepo.existsAndActiveById(id);
  }

  @Override
  public ConnectionDto updateConnection(String id, ConnectionAction action) {
    switch (action) {
      case ACCEPT -> {
        var pendingConnection = pendingConnectionRepo.findByIdAndState(id, State.ACTIVE)
          .orElseThrow(() -> new RuntimeException("Connection not found"));

        var newConnection = new AcceptedConnection(pendingConnection);

        //  Generate clientId and clientSecret
        var clientId = UUID.randomUUID().toString().replace("-", "");
        newConnection.setClientId(clientId);

        var clientSecret = PasswordUtils.generateSecurePassword();
        newConnection.setClientSecret(passwordEncoder.encode(clientSecret));

        // Save new connection and delete pending connection
        var saved = acceptedConnectionRepo.save(newConnection);
        pendingConnectionRepo.deleteById(pendingConnection.getId());

        var res = new ConnectionDto(saved);
        res.setClientSecret(clientSecret);
        return res;
      }
      case REJECT -> {
        pendingConnectionRepo.updateState(id, State.INACTIVE);
        return new ConnectionDto(pendingConnectionRepo.findById(id).orElseThrow(() -> new RuntimeException("Connection not found")));
      }
      default -> throw new IllegalArgumentException("Invalid action: " + action);
    }
  }

  @Override
  public ConnectionListResponseDto getConnections(ConnectionStatus status) {
    if (status != null) {
      switch (status) {
        case PENDING -> {
          var connectionList = pendingConnectionRepo.findAllByState(State.ACTIVE);
          return new ConnectionListResponseDto(new ArrayList<>(connectionList));
        }
        case ACCEPTED -> {
          var connectionList = acceptedConnectionRepo.findAllByState(State.ACTIVE);
          return new ConnectionListResponseDto(new ArrayList<>(connectionList));
        }
        case REJECTED -> {
          var connectionList = pendingConnectionRepo.findAllByState(State.INACTIVE);
          return new ConnectionListResponseDto(new ArrayList<>(connectionList));
        }
      }
    }
    List<BaseConnection> connectionList = new ArrayList<>();
    connectionList.addAll(acceptedConnectionRepo.findAllByState(State.ACTIVE));
    connectionList.addAll(pendingConnectionRepo.findAllByState(State.ACTIVE));
    connectionList.addAll(pendingConnectionRepo.findAllByState(State.INACTIVE));
    return new ConnectionListResponseDto(connectionList);
  }
}
