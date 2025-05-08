package com.example.userpost.dto.request.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateGroupRequestDto {
  private String name;
  private String thumbnail;
}
