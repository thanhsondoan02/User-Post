package com.example.userpost.service;

import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.dto.request.post.UpdatePostRequestDto;
import com.example.userpost.dto.response.post.PostResponseDto;
import com.example.userpost.model.post.Post;

public interface IPostService {
  PostResponseDto createPost(String title, String content);
  boolean validateCreateFields(CreatePostRequestDto request);
  boolean validateCreateFormat(CreatePostRequestDto request);
  boolean validateUpdateFields(UpdatePostRequestDto request);
  boolean validateUpdateFormat(UpdatePostRequestDto request);
  Post getPostById(String postId);
  PostResponseDto updatePost(String id, String title, String content);
  void deletePost(String id);
}
