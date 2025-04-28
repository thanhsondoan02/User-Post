package com.example.userpost.service.impl;

import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.dto.response.post.PostResponseDto;
import com.example.userpost.model.post.Post;
import com.example.userpost.repository.PostRepository;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IPostService;
import com.example.userpost.util.ValidationUtils;
import org.springframework.stereotype.Service;

@Service
public class PostService implements IPostService {

  private final PostRepository postRepository;
  private final IAuthService authService;

  public PostService(PostRepository postRepository, IAuthService authService) {
    this.postRepository = postRepository;
    this.authService = authService;
  }

  @Override
  public PostResponseDto createPost(String title, String content) {
    Post post = new Post(title, content, authService.getAuthUser().getId());
    return new PostResponseDto(postRepository.save(post));
  }

  @Override
  public boolean validateCreateFields(CreatePostRequestDto request) {
    return request.getTitle() != null && request.getContent() != null;
  }

  @Override
  public boolean validateCreateFormat(CreatePostRequestDto request) {
    return ValidationUtils.isPostTitleValid(request.getTitle())
      && ValidationUtils.isPostContentValid(request.getContent());
  }
}
