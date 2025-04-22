package com.example.userpost.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseResponse<T> {
  private Meta meta;
  private T data;
}
