package com.example.userpost.controller;

import com.example.userpost.constant.*;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
import com.example.userpost.dto.request.openid.webhook.RegisterWebhookRequestDto;
import com.example.userpost.dto.response.openid.event.ScopeListDto;
import com.example.userpost.dto.response.openid.event.EventDto;
import com.example.userpost.dto.response.openid.event.ScopeDto;
import com.example.userpost.dto.response.openid.webhook.WebhookListResponseDto;
import com.example.userpost.model.openid.EventScope;
import com.example.userpost.service.IApiService;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IOpenIdService;
import com.example.userpost.util.ResponseBuilder;
import com.example.userpost.util.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OpenIdController {

  private final IAuthService authService;
  private final IOpenIdService openIdService;
  private final IApiService apiService;

  public OpenIdController(IAuthService authService, IOpenIdService openIdService, IApiService apiService) {
    this.authService = authService;
    this.openIdService = openIdService;
    this.apiService = apiService;
  }

  @PostMapping("/connections")
  public ResponseEntity<?> requestConnect(@RequestBody ConnectRequestDto request) {
    var name = request.getName();
    var domain = request.getDomain();
    var callbackUrl = request.getCallbackUrl();

    if (name == null || name.isBlank()
      || !ValidationUtils.isValidDomain(domain)
      || !ValidationUtils.isSameHost(domain, callbackUrl)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.BAD_REQUEST);
    }

    return ResponseBuilder.success(openIdService.addPendingConnections(request));
  }

  @GetMapping("/connections")
  public ResponseEntity<?> getConnections(@RequestParam(required = false) String status) {
    ConnectionStatus connectionStatus;
    if (status != null) {
      try {
        connectionStatus = ConnectionStatus.fromString(status);
      } catch (IllegalArgumentException e) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.BAD_REQUEST);
      }
    } else {
      connectionStatus = null;
    }

    return ResponseBuilder.success(openIdService.getConnections(connectionStatus));
  }

  @PostMapping("/connections/{id}")
  public ResponseEntity<?> updateConnection(@PathVariable("id") String id, @RequestBody UpdateConnectionRequestDto request) {
    ConnectionAction action;
    try {
      action = ConnectionAction.fromString(request.getAction());
    } catch (IllegalArgumentException e) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.BAD_REQUEST);
    }

    if (!openIdService.isConnectionExistAndPending(id)) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.CONNECTION_NOT_FOUND);
    }

    // Update connection status in database
    var response = openIdService.updateConnection(id, action);

    // Call to server A to notify about the connection update
    if (action == ConnectionAction.ACCEPT) {
      request.setClientId(response.getClientId());
      request.setClientSecret(response.getClientSecret());
    }
    apiService.updateConnection(response.getCallbackUrl(), request);

    return ResponseBuilder.success(response);
  }

  @PostMapping("/events")
  public ResponseEntity<?> createEvent(@RequestBody EventDto request) {
    return ResponseBuilder.success(openIdService.createEvent(request));
  }

  @PostMapping("/scopes")
  public ResponseEntity<?> createScope(@RequestBody ScopeDto request) {
    return ResponseBuilder.success(openIdService.createScope(request));
  }

  @PostMapping("/event_scopes")
  public ResponseEntity<?> mapScopesToEvents(@RequestBody ScopeListDto request) {
    return ResponseBuilder.success(openIdService.mapScopeToEvent(request));
  }

  @GetMapping("/events")
  public ResponseEntity<?> getEvents() {
    var res = new ScopeListDto(openIdService.getAllScopeEvents());
    return ResponseBuilder.success(res);
  }

  @PostMapping("/webhooks")
  public ResponseEntity<?> registerWebhook(@RequestBody RegisterWebhookRequestDto request) {
    var connection = authService.getAuthClient();

    if (!ValidationUtils.isSameHost(connection.getDomain(), request.getRedirectUrl())) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.INVALID_REDIRECT_URL);
    }

    EventScope eventScope;
    try {
      var eventType = HookEvent.fromCode(request.getEvent());
      var scopeType = HookScope.fromCode(request.getScope());
      eventScope = openIdService.getEventScope(eventType, scopeType)
          .orElseThrow(() -> new IllegalArgumentException(MessageConst.INVALID_EVENT_OR_SCOPE));
    } catch (IllegalArgumentException ignored) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.INVALID_EVENT_OR_SCOPE);
    }

    var res = openIdService.createWebhook(request, eventScope);
    return ResponseBuilder.success(res);
  }

  @GetMapping("/webhooks")
  public ResponseEntity<?> getWebhooks() {
    var connection = authService.getAuthClient();
    var webhooks = openIdService.getWebhooksByConnectionId(connection.getId());
    return ResponseBuilder.success(new WebhookListResponseDto(webhooks));
  }
}
