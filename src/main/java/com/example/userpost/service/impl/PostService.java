package com.example.userpost.service.impl;

import com.example.userpost.dto.post.CreatePostRequest;
import com.example.userpost.dto.post.PostDto;
import com.example.userpost.model.post.Post;
import com.example.userpost.repository.PostRepository;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IPostService;
import com.example.userpost.util.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService implements IPostService {

  private final PostRepository postRepository;
  private final IAuthService authService;

  public PostService(PostRepository postRepository, IAuthService authService) {
    this.postRepository = postRepository;
    this.authService = authService;
  }

  @Override
  public PostDto createPost(String title, String content) {
    Post post = Post.builder()
      .title(title)
      .content(content)
      .userId(authService.getAuthUser().getId())
      .build();
    return convertToPostDto(postRepository.save(post));
  }

  @Override
  public boolean validateCreateFields(CreatePostRequest request) {
    return request.getTitle() != null && request.getContent() != null;
  }

  @Override
  public boolean validateCreateFormat(CreatePostRequest request) {
    return ValidationUtils.isPostTitleValid(request.getTitle())
      && ValidationUtils.isPostContentValid(request.getContent());
  }

  private List<PostDto> convertToListPostDto(List<Post> users) {
    return users.stream()
      .map(this::convertToPostDto)
      .toList();
  }

  private PostDto convertToPostDto(Post post) {
    return PostDto.builder()
      .id(post.getId())
      .title(post.getTitle())
      .content(post.getContent())
      .userId(post.getUserId())
      .build();
  }
}
