package com.example.userpost.service;

import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.dto.response.post.PostResponseDto;

public interface IPostService {
  PostResponseDto createPost(String title, String content);
  boolean validateCreateFields(CreatePostRequestDto request);
  boolean validateCreateFormat(CreatePostRequestDto request);
}
