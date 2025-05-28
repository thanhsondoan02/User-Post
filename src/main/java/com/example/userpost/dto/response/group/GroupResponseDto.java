package com.example.userpost.dto.response.group;

import com.example.userpost.model.group.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupResponseDto {
  private String id;
  private String name;
  private String thumbnail;
  private List<GroupUserResponseDto> users;

  public GroupResponseDto(Group group, boolean includeUserDetails) {
    this.id = group.getId();
    this.name = group.getName();
    this.thumbnail = group.getThumbnail();
    this.users = group.getGroupUsers().stream()
      .map(v -> new GroupUserResponseDto(v, includeUserDetails))
      .toList();
  }
}
