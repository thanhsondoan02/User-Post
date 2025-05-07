package com.example.userpost.dto.request.group;

import com.example.userpost.constant.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupUserRequestDto {
  private String userId;
  private String role;

  public GroupRole getRole() {
    return GroupRole.fromString(role);
  }
}
