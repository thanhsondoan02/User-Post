package com.example.userpost.dto.response.auth;

import com.example.userpost.dto.response.openid.connect.ConnectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerJwtResponseDto {
  private String refreshToken;
  private String accessToken;
  private ConnectionDto server;
}

