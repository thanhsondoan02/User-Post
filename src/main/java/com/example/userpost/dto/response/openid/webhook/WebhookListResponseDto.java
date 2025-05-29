package com.example.userpost.dto.response.openid.webhook;

import com.example.userpost.model.openid.Webhook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WebhookListResponseDto {
  private List<WebhookResponseDto> webhooks;

  public WebhookListResponseDto(List<Webhook> webhooks) {
    this.webhooks = webhooks.stream()
      .map(WebhookResponseDto::new)
      .toList();
  }
}

