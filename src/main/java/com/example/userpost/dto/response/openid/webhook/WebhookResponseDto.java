package com.example.userpost.dto.response.openid.webhook;

import com.example.userpost.dto.response.openid.event.EventDto;
import com.example.userpost.dto.response.openid.event.ScopeDto;
import com.example.userpost.model.openid.Webhook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebhookResponseDto {
  private EventDto event;
  private ScopeDto scope;
  private String scopeValue;
  private String redirectUrl;

  public WebhookResponseDto(Webhook webhook) {
    event = new EventDto();
    event.setName(webhook.getEvent().getName());

    scope = new ScopeDto();
    scope.setName(webhook.getScope().getName());

    scopeValue = webhook.getScopeValue();
    redirectUrl = webhook.getRedirectUrl();
  }
}

