package com.example.userpost.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
  private int status;
  private String message;
  private T data;
}
