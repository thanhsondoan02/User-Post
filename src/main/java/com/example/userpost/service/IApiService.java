package com.example.userpost.service;


import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
import com.example.userpost.dto.response.base.BaseResponseDto;
import com.example.userpost.dto.response.openid.connect.ConnectionDto;

public interface IApiService {
  BaseResponseDto<?> updateConnection(String url, UpdateConnectionRequestDto body);
}
