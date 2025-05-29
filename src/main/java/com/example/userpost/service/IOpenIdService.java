package com.example.userpost.service;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.HookScope;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.request.openid.webhook.RegisterWebhookRequestDto;
import com.example.userpost.dto.response.openid.connect.ConnectionDto;
import com.example.userpost.dto.response.openid.connect.ConnectionListResponseDto;
import com.example.userpost.dto.response.openid.event.ScopeListDto;
import com.example.userpost.dto.response.openid.event.EventDto;
import com.example.userpost.dto.response.openid.event.ScopeDto;
import com.example.userpost.dto.response.openid.webhook.WebhookResponseDto;
import com.example.userpost.model.openid.Event;
import com.example.userpost.model.openid.EventScope;
import com.example.userpost.model.openid.Scope;

import java.util.List;
import java.util.Optional;

public interface IOpenIdService {
  ConnectionDto addPendingConnections(ConnectRequestDto request);

  boolean isConnectionExistAndPending(String id);

  ConnectionDto updateConnection(String id, ConnectionAction action);

  ConnectionListResponseDto getConnections(ConnectionStatus status);

  EventDto createEvent(EventDto request);

  ScopeDto createScope(ScopeDto request);

  ScopeListDto mapScopeToEvent(ScopeListDto request);

  List<Scope> getAllScopeEvents();

  WebhookResponseDto createWebhook(RegisterWebhookRequestDto request, EventScope eventScope);

  Optional<EventScope> getEventScope(HookEvent event, HookScope scope);
}
