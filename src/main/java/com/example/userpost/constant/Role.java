package com.example.userpost.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum Role {
  USER(0),
  ADMIN(1);

  private final int code;

  public static Role fromCode(int code) {
    for (Role v : values()) {
      if (v.code == code) return v;
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }
}
