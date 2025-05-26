package com.example.userpost.controller;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.MessageConst;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
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
      || !ValidationUtils.isValidCallbackUrl(domain, callbackUrl)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.BAD_REQUEST);
    }

    return ResponseBuilder.success(openIdService.addPendingConnections(request));
  }

  @GetMapping("/connections")
  public ResponseEntity<?> getConnections(@RequestParam(required = false) String status) {
    if (!authService.isAdmin()) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }

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
    if (!authService.isAdmin()) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }

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
}
