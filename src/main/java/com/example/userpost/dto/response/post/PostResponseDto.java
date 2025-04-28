package com.example.userpost.dto.response.post;

import com.example.userpost.model.post.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDto {
  private String id;
  private String title;
  private String content;
  private String userId;

  public PostResponseDto(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.userId = post.getUserId();
  }
}

