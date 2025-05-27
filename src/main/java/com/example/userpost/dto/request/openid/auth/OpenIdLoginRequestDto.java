package com.example.userpost.dto.request.openid.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OpenIdLoginRequestDto {
  private String clientId;
  private String clientSecret;
}
