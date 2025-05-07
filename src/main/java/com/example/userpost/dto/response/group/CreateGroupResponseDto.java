package com.example.userpost.dto.response.group;

import com.example.userpost.model.group.GroupUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateGroupResponseDto {
  private String id;
  private String name;
  private String thumbnail;
  private List<GroupUserResponseDto> users;

  public CreateGroupResponseDto(String id, String name, String thumbnail, List<GroupUser> users) {
    this.id = id;
    this.name = name;
    this.thumbnail = thumbnail;
    this.users = users.stream()
      .map(GroupUserResponseDto::new)
      .toList();
  }
}

