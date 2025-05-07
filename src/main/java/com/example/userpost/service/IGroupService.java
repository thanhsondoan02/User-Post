package com.example.userpost.service;

import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.response.group.CreateGroupResponseDto;

public interface IGroupService {
  CreateGroupResponseDto createGroup(CreateGroupRequestDto request);
}
