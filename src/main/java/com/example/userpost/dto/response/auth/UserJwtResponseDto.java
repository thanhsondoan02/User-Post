package com.example.userpost.dto.response.auth;

import com.example.userpost.dto.response.user.UserResponseDto;
import com.example.userpost.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserJwtResponseDto {
  private String refreshToken;
  private String accessToken;
  private UserResponseDto user;
}

