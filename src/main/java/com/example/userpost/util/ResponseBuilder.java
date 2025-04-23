package com.example.userpost.util;

import com.example.userpost.dto.base.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
  public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
    return build(HttpStatus.OK.value(), MessageConst.SUCCESS, data);
  }

  public static <T> ResponseEntity<BaseResponse<T>> error(int code, String message) {
    return build(code, message, null);
  }

  public static <T> ResponseEntity<BaseResponse<T>> internalServerError() {
    return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageConst.INTERNAL_SERVER_ERROR);
  }

  public static <T> ResponseEntity<BaseResponse<T>> build(int code, String message, T data) {
    return ResponseEntity
      .status(code)
      .body(new BaseResponse<>(code, message, data));
  }
}
