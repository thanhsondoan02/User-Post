package com.example.userpost.util;

import com.example.userpost.dto.base.BaseResponse;
import com.example.userpost.dto.base.Meta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
  public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
    Meta meta = new Meta(HttpStatus.OK.value(), "Success");
    return ResponseEntity.ok(new BaseResponse<>(meta, data));
  }

  public static <T> ResponseEntity<BaseResponse<T>> error(int code, String message) {
    return ResponseEntity
      .status(code)
      .body(new BaseResponse<>(new Meta(code, message), null));
  }

  public static <T> ResponseEntity<BaseResponse<T>> internalServerError() {
    String message = "Internal Server Error";
    return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
  }
}
