package com.example.userpost.service.impl;

import com.example.userpost.constant.State;
import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.dto.request.post.UpdatePostRequestDto;
import com.example.userpost.dto.response.post.PostResponseDto;
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

  @Override
  public boolean validateUpdateFields(UpdatePostRequestDto request) {
    return request.getTitle() != null && request.getContent() != null;
  }

  @Override
  public boolean validateUpdateFormat(UpdatePostRequestDto request) {
    return ValidationUtils.isPostTitleValid(request.getTitle())
      && ValidationUtils.isPostContentValid(request.getContent());
  }

  @Override
  public Post getPostById(String id) {
    return postRepository.findActiveById(id);
  }

  @Override
  public PostResponseDto updatePost(String id, String title, String content) {
    Post post = postRepository.findById(id).orElse(null);
    if (post != null) {
      post.setTitle(title);
      post.setContent(content);
      return new PostResponseDto(postRepository.save(post));
    } else {
      return null;
    }
  }

  @Override
  public void deletePost(String id) {
    Post post = postRepository.findById(id).orElse(null);
    if (post != null) {
      post.setState(State.INACTIVE);
      postRepository.save(post);
    }
  }

  @Override
  public List<Post> getAll() {
    return postRepository.findActivePost();
  }

  @Override
  public List<Post> searchPostByTitle(String title) {
    return postRepository.findByTitle(title);
  }

  @Override
  public List<Post> searchPostByUser(String userId) {
    return postRepository.findByUserId(userId);
  }

  @Override
  public List<Post> searchPostByTitleAndUser(String title, String userId) {
    return postRepository.findByTitleAndUserId(title, userId);
  }
}
