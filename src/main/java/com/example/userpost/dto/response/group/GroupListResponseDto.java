package com.example.userpost.dto.response.group;

import com.example.userpost.model.group.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupListResponseDto {
  private int total;
  private List<GroupResponseDto> groups;

  public GroupListResponseDto(List<Group> groups, boolean includeUserDetails) {
    this.total = groups.size();
    this.groups = groups.stream()
      .map(v -> new GroupResponseDto(v, includeUserDetails))
      .toList();
  }
}

