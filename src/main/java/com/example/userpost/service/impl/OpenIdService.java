package com.example.userpost.service.impl;

import com.example.userpost.constant.*;
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

  private final AcceptedConnectionRepository acceptedConnectionRepo;
  private final PendingConnectionRepository pendingConnectionRepo;
  private final PasswordEncoder passwordEncoder;
  private final EventRepository eventRepository;
  private final ScopeRepository scopeRepository;
  private final WebhookRepository webhookRepository;
  private final AuthService authService;

  public OpenIdService(
    AcceptedConnectionRepository acceptedConnectionRepo,
    PendingConnectionRepository pendingConnectionRepo,
    PasswordEncoder passwordEncoder,
    EventRepository eventRepository,
    ScopeRepository scopeRepository,
    WebhookRepository webhookRepository,
    AuthService authService
  ) {
    this.acceptedConnectionRepo = acceptedConnectionRepo;
    this.pendingConnectionRepo = pendingConnectionRepo;
    this.passwordEncoder = passwordEncoder;
    this.eventRepository = eventRepository;
    this.scopeRepository = scopeRepository;
    this.webhookRepository = webhookRepository;
    this.authService = authService;
  }

  @Override
  public boolean isConnectionWithDomainExist(String domain) {
    return pendingConnectionRepo.existsByDomainAndState(domain, State.ACTIVE) ||
           acceptedConnectionRepo.existsByDomainAndState(domain, State.ACTIVE);
  }

  @Override
  public ConnectionDto addPendingConnections(ConnectRequestDto request) {
    var connection = pendingConnectionRepo.getByDomain(request.getDomain());

    if (connection.isPresent()) {
      if (connection.get().getState() == State.ACTIVE) {
        throw new RuntimeException("Connection with this domain already exists and is pending.");
      } else {
        pendingConnectionRepo.delete(connection.get());
      }
    }

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

    // Check if this connection is register this type of event and scope before and delete it if exists
    webhookRepository.findInactive(eventScope, connection).ifPresent(webhookRepository::delete);

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
