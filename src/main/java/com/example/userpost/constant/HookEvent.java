package com.example.userpost.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum HookEvent {
  CREATE(0),
  UPDATE(1),
  DELETE(2);

  private final int code;

  public static HookEvent fromCode(int code) {
    for (HookEvent v : values()) {
      if (v.code == code) return v;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }

  public static HookEvent fromString(String event) {
    for (HookEvent v : values()) {
      if (v.toString().equalsIgnoreCase(event)) return v;
    }
    throw new IllegalArgumentException("Invalid event: " + event);
  }
}
