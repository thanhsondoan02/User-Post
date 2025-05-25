package com.example.userpost.service.impl;

import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
import com.example.userpost.dto.response.base.BaseResponseDto;
import com.example.userpost.dto.response.openid.connect.ConnectionDto;
import com.example.userpost.service.IApiService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService implements IApiService {

  private final RestTemplate restTemplate;

  public ApiService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public BaseResponseDto<ConnectionDto> updateConnection(String url, UpdateConnectionRequestDto body) {
    var request = new HttpEntity<>(body);
    var response = restTemplate.exchange(
      url, HttpMethod.POST, request,
      new ParameterizedTypeReference<BaseResponseDto<ConnectionDto>>() {}
    );
    return response.getBody();
  }
}
