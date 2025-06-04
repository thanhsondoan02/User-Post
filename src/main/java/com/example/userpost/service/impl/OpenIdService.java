package com.example.userpost.service.impl;

import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.HookScope;
import com.example.userpost.constant.State;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.request.openid.webhook.RegisterWebhookRequestDto;
import com.example.userpost.dto.response.openid.connect.ConnectionDto;
import com.example.userpost.dto.response.openid.connect.ConnectionListResponseDto;
import com.example.userpost.dto.response.openid.event.EventDto;
import com.example.userpost.dto.response.openid.event.ScopeDto;
import com.example.userpost.dto.response.openid.event.ScopeListDto;
import com.example.userpost.dto.response.openid.webhook.WebhookResponseDto;
import com.example.userpost.model.openid.*;
import com.example.userpost.repository.*;
import com.example.userpost.service.IOpenIdService;
import com.example.userpost.util.PasswordUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OpenIdService implements IOpenIdService {

  private final ConnectionRepository connectionRepository;
  private final PasswordEncoder passwordEncoder;
  private final EventRepository eventRepository;
  private final ScopeRepository scopeRepository;
  private final WebhookRepository webhookRepository;
  private final AuthService authService;
  private final ServerRepository serverRepository;
  private final ServerTokenRepository serverTokenRepository;

  public OpenIdService(
    ConnectionRepository connectionRepository,
    PasswordEncoder passwordEncoder,
    EventRepository eventRepository,
    ScopeRepository scopeRepository,
    WebhookRepository webhookRepository,
    AuthService authService,
    ServerRepository serverRepository,
    ServerTokenRepository serverTokenRepository
  ) {
    this.connectionRepository = connectionRepository;
    this.passwordEncoder = passwordEncoder;
    this.eventRepository = eventRepository;
    this.scopeRepository = scopeRepository;
    this.webhookRepository = webhookRepository;
    this.authService = authService;
    this.serverRepository = serverRepository;
    this.serverTokenRepository = serverTokenRepository;
  }

  @Override
  public boolean isConnectionWithDomainExist(String domain) {
    return serverRepository.isConnectionWithServerExists(domain);
  }

  @Override
  public ConnectionDto addPendingConnections(ConnectRequestDto request) {
    if (connectionRepository.existsById(request.getId())) {
      throw new RuntimeException("Connection with this ID already exists");
    }

    var existingServer = serverRepository.findByDomain(request.getDomain());
    Server server;

    if (existingServer.isPresent()) {
      server = existingServer.get();
    } else {
      server = new Server();
      server.setDomain(request.getDomain());
      server = serverRepository.save(server);
    }

    var newConnection = new Connection();
    newConnection.setId(request.getId());
    newConnection.setTargetServer(server);
    newConnection.setName(request.getName());
    newConnection.setCallbackUrl(request.getCallbackUrl());
    newConnection.setStatus(ConnectionStatus.PENDING);

    return new ConnectionDto(connectionRepository.save(newConnection));
  }

  @Override
  public Optional<Connection> getConnectionById(String id) {
    return connectionRepository.findActiveById(id);
  }

  @Override
  public ConnectionDto acceptConnection(String id) {
    var connection = connectionRepository.findActiveById(id)
      .orElseThrow(() -> new RuntimeException("Connection not found"));

    String clientSecret;

    if (connection.getClientId() == null) {
      // Generate clientId and clientSecret
      var clientId = UUID.randomUUID().toString().replace("-", "");
      clientSecret = PasswordUtils.generateSecurePassword();
      connection.setClientId(clientId);
      connection.setClientSecret(passwordEncoder.encode(clientSecret));
    } else {
      clientSecret = null;
    }

    // Save updated connection to database
    connection.setStatus(ConnectionStatus.ACCEPTED);
    var saved = connectionRepository.save(connection);

    // Return connection dto with raw clientSecret
    var res = new ConnectionDto(saved);
    if (clientSecret != null) {
      res.setClientSecret(clientSecret);
    }
    return res;
  }

  @Override
  public ConnectionDto rejectConnection(String id) {
    var connection = connectionRepository.findActiveById(id)
      .orElseThrow(() -> new RuntimeException("Connection not found"));
    connection.setStatus(ConnectionStatus.REJECTED);

    serverTokenRepository.deleteTokensByConnectionId(id);
    webhookRepository.deleteWebhooksByConnectionId(id);

    return new ConnectionDto(connectionRepository.save(connection));
  }

  @Override
  public ConnectionListResponseDto getFilteredConnections(ConnectionStatus status) {
    return new ConnectionListResponseDto(connectionRepository.findActiveByStatus(status));
  }

  @Override
  public ConnectionListResponseDto getAllConnections() {
    var connectionList = new ArrayList<Connection>();
    connectionList.addAll(connectionRepository.findActiveByStatus(ConnectionStatus.PENDING));
    connectionList.addAll(connectionRepository.findActiveByStatus(ConnectionStatus.ACCEPTED));
    connectionList.addAll(connectionRepository.findActiveByStatus(ConnectionStatus.REJECTED));
    return new ConnectionListResponseDto(connectionList);
  }

  @Override
  public EventDto createEvent(EventDto request) {
    var saved = eventRepository.save(request.toEvent());
    return new EventDto(saved);
  }

  @Override
  public ScopeDto createScope(ScopeDto request) {
    var saved = scopeRepository.save(request.toScope());
    return new ScopeDto(saved);
  }

  @Override
  public ScopeListDto mapScopeToEvent(ScopeListDto request) {
    List<Scope> scopes = new ArrayList<>();

    for (var scopeDto : request.getScopes()) {
      var scope = scopeRepository.findActiveByType(scopeDto.getType())
        .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeDto.getType()));
      if (scopeDto.getEvents() != null) {
        List<EventScope> events = new ArrayList<>();
        for (var eventDto : scopeDto.getEvents()) {
          var event = eventRepository.findActiveByType(eventDto.getType())
            .orElseThrow(() -> new RuntimeException("Event not found: " + eventDto.getType()));
          events.add(new EventScope(event, scope));
        }
        scope.getEvents().addAll(events);
      }
      scopes.add(scope);
    }

    List<Scope> saved = scopeRepository.saveAll(scopes);

    return new ScopeListDto(saved);
  }

  @Override
  public List<Scope> getAllScopeEvents() {
    return scopeRepository.getActiveScopes();
  }

  @Override
  public WebhookResponseDto createWebhook(RegisterWebhookRequestDto request, EventScope eventScope) {
    var connection = authService.getAuthClient();

    var webhook = new Webhook();
    webhook.setRedirectUrl(request.getRedirectUrl());
    webhook.setEventScope(eventScope);
    webhook.setConnection(connection);

    var save = webhookRepository.save(webhook);
    return new WebhookResponseDto(save);
  }

  @Override
  public Optional<Webhook> getWebhookById(String id) {
    return webhookRepository.findActiveById(id);
  }

  @Override
  public List<Webhook> getWebhooksByConnectionId(String connectionId) {
    return webhookRepository.findByConnectionId(connectionId);
  }

  @Override
  public void deleteWebhook(String id) {
    var webhook = webhookRepository.findActiveById(id)
      .orElseThrow(() -> new RuntimeException("Webhook not found: " + id));
    webhook.setState(State.INACTIVE);
    webhookRepository.save(webhook);
  }

  @Override
  public Optional<EventScope> getEventScope(HookEvent event, HookScope scope) {
    return eventRepository.findActiveEventScope(event, scope);
  }
}
