package com.example.userpost.dto.response.openid.event;

import com.example.userpost.constant.HookScope;
import com.example.userpost.model.openid.EventScope;
import com.example.userpost.model.openid.Scope;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ScopeDto {
  private Integer code;
  private String name;
  private String description;
  private List<EventDto> events;

  public ScopeDto(Scope scope) {
    code = scope.getType().getCode();
    name = scope.getName();
    description = scope.getDescription();
    if (scope.getEvents() != null) {
      events = scope.getEvents().stream()
        .map(v -> new EventDto(v.getEvent()))
        .toList();
    }
  }

  public Scope toScope() {
    Scope scope = new Scope();
    scope.setType(HookScope.fromCode(code));
    scope.setName(this.name);
    scope.setDescription(this.description);
    return scope;
  }

  @JsonIgnore
  public HookScope getType() {
    return HookScope.fromCode(code);
  }
}

