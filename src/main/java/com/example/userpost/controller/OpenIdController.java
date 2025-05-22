package com.example.userpost.controller;

import com.example.userpost.constant.ConnectionAction;
import com.example.userpost.constant.MessageConst;
import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
import com.example.userpost.dto.request.openid.connect.ConnectRequestDto;
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

  IAuthService authService;
  IOpenIdService openIdService;

  public OpenIdController(IAuthService authService, IOpenIdService openIdService) {
    this.authService = authService;
    this.openIdService = openIdService;
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

    openIdService.addPendingConnections(request);
    return ResponseBuilder.success();
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

    // call to server A

    return ResponseBuilder.success(openIdService.updateConnection(id, action));
  }
}
