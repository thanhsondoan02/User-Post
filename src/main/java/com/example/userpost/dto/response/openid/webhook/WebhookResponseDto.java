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
  private String id;
  private ScopeDto scope;
  private EventDto event;
  private String redirectUrl;

  public WebhookResponseDto(Webhook webhook) {
    id = webhook.getId();
    redirectUrl = webhook.getRedirectUrl();

    var eventScope = webhook.getEventScope();

    var eventEntity = eventScope.getEvent();
    event = new EventDto();
    event.setCode(eventEntity.getType().getCode());
    event.setName(eventEntity.getName());

    var scopeEntity = eventScope.getScope();
    scope = new ScopeDto();
    scope.setCode(scopeEntity.getType().getCode());
    scope.setName(scopeEntity.getName());

  }
}

