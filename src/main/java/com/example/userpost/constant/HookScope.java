package com.example.userpost.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HookScope {
  USERS(0),
  POSTS(1),
  GROUPS(2),
  GROUP_MEMBERS(3);

  private final int code;

  public static HookScope fromCode(int code) {
    for (HookScope v : values()) {
      if (v.code == code) return v;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }

  public static HookScope fromString(String scope) {
    for (HookScope v : values()) {
      if (v.toString().equalsIgnoreCase(scope)) return v;
    }
    throw new IllegalArgumentException("Invalid scope: " + scope);
  }
}
