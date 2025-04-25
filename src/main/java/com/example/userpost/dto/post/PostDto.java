package com.example.userpost.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostDto {
  private String id;
  private String title;
  private String content;
  private String userId;
}

