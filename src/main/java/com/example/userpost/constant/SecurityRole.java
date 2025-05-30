package com.example.userpost.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SecurityRole {
  USER(0),
  USER_ADMIN(1),
  CLIENT(2);

  private final int code;

  public static SecurityRole fromCode(int code) {
    for (SecurityRole v : values()) {
      if (v.code == code) return v;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }

  public static SecurityRole fromString(String role) {
    for (SecurityRole v : values()) {
      if (Objects.equals(v.toString(), role)) return v;
    }
    throw new IllegalArgumentException("Invalid security role: " + role);
  }

  public String withPrefix() {
    return "ROLE_" + this;
  }
}
