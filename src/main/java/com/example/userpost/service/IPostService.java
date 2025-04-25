package com.example.userpost.service;

import com.example.userpost.dto.post.CreatePostRequest;
import com.example.userpost.dto.post.PostDto;

public interface IPostService {
  PostDto createPost(String title, String content);
  boolean validateCreateFields(CreatePostRequest request);
  boolean validateCreateFormat(CreatePostRequest request);
}
