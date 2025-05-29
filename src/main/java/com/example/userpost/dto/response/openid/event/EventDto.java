package com.example.userpost.dto.response.openid.event;

import com.example.userpost.constant.HookEvent;
import com.example.userpost.model.openid.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventDto {
  private Integer code;
  private String name;
  private String description;

  public EventDto(Event event) {
    this.code = event.getType().getCode();
    this.name = event.getName();
    this.description = event.getDescription();
  }

  public Event toEvent() {
    return new Event(code, name, description);
  }

  @JsonIgnore
  public HookEvent getType() {
    return HookEvent.fromCode(code);
  }
}

