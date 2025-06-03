package com.example.userpost.service.impl;

import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.HookScope;
import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
import com.example.userpost.dto.request.openid.webhook.WebhookDto;
import com.example.userpost.dto.response.group.GroupResponseDto;
import com.example.userpost.dto.response.openid.event.EventDto;
import com.example.userpost.dto.response.openid.event.ScopeDto;
import com.example.userpost.dto.response.post.PostResponseDto;
import com.example.userpost.dto.response.user.UserResponseDto;
import com.example.userpost.repository.ConnectionRepository;
import com.example.userpost.repository.EventRepository;
import com.example.userpost.repository.ScopeRepository;
import com.example.userpost.repository.WebhookRepository;
import com.example.userpost.service.IApiService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ApiService implements IApiService {

  private final ExecutorService executorService = Executors.newFixedThreadPool(10);

  private final RestTemplate restTemplate;
  private final WebhookRepository webhookRepository;
  private final ConnectionRepository connectionRepository;
  private final EventRepository eventRepository;
  private final ScopeRepository scopeRepository;

  public ApiService(
    RestTemplate restTemplate, WebhookRepository webhookRepository,
    ConnectionRepository connectionRepository,
    EventRepository eventRepository, ScopeRepository scopeRepository
  ) {
    this.restTemplate = restTemplate;
    this.webhookRepository = webhookRepository;
    this.connectionRepository = connectionRepository;
    this.eventRepository = eventRepository;
    this.scopeRepository = scopeRepository;
  }

  @Override
  public void updateConnection(String url, UpdateConnectionRequestDto body) {
    handleCall(url, HttpMethod.POST, new HttpEntity<>(body));
  }

  @Override
  public void sendWebhookScopeUsers(HookEvent eventType, UserResponseDto data) {
    sendToAllWebHooks(HookScope.USERS, eventType, data);
  }

  @Override
  public void sendWebhookScopeGroups(HookEvent eventType, GroupResponseDto data) {
    sendToAllWebHooks(HookScope.GROUPS, eventType, data);
  }

  @Override
  public void sendWebhookScopeGroupMembers(HookEvent eventType, GroupResponseDto data) {
    sendToAllWebHooks(HookScope.GROUP_MEMBERS, eventType, data);
  }

  @Override
  public void sendWebhookScopePosts(HookEvent eventType, PostResponseDto data) {
    sendToAllWebHooks(HookScope.POSTS, eventType, data);
  }

  private <T> void sendToAllWebHooks(HookScope scopeType, HookEvent eventType, T data) {
    executorService.submit(() -> {

      // Check scope is active
      var scope = scopeRepository.findActiveByType(scopeType).orElse(null);
      if (scope == null) return;

      // Check event is active
      var event = eventRepository.findActiveByType(eventType).orElse(null);
      if (event == null) return;

      // Same body for all clients (only connectionId will change)
      var body = new WebhookDto<T>();
      body.setData(data);

      var eventDto = new EventDto();
      eventDto.setCode(event.getType().getCode());
      eventDto.setName(event.getName());
      body.setEvent(eventDto);

      var scopeDto = new ScopeDto();
      scopeDto.setCode(scope.getType().getCode());
      scopeDto.setName(scope.getName());
      body.setScope(scopeDto);

      var webhooks = webhookRepository.findActiveByScopeEvent(scope, event);
      for (var webhook : webhooks) {
        var connectionId = webhook.getConnection().getId();
        var isConnectionAccepted = connectionRepository
          .findActiveByIdAndStatus(connectionId, ConnectionStatus.ACCEPTED)
          .isPresent();
        if (isConnectionAccepted) { // Only send to accepted connections
          body.setConnectionId(connectionId);
          sendEvent(webhook.getRedirectUrl(), body);
        }
      }
    });
  }

  private <T> void sendEvent(String url, WebhookDto<T> body) {
    handleCall(url, HttpMethod.POST, new HttpEntity<>(body));
  }

  private void handleCall(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity) {
    try {
      restTemplate.exchange(url, method, requestEntity, String.class);
    } catch (Exception ignored) {
    }
  }
}
