package com.example.userpost.dto.request.openid.webhook;

import com.example.userpost.dto.response.openid.event.EventDto;
import com.example.userpost.dto.response.openid.event.ScopeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebhookDto<T> {
  String connectionId;
  ScopeDto scope;
  EventDto event;
  T data;
}
