package com.example.userpost.dto.request.openid.webhook;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterWebhookRequestDto {
  private String event;
  private String scope;
  private String redirectUrl;
}
