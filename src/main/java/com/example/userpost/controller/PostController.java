package com.example.userpost.controller;

import com.example.userpost.constant.MessageConst;
import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.dto.request.post.UpdatePostRequestDto;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IPostService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final IPostService postService;
  private final IAuthService authService;

  public PostController(IPostService postService, IAuthService authService) {
    this.postService = postService;
    this.authService = authService;
  }

  @PostMapping
  public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto request) {
    if (!postService.validateCreateFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    } else if (!postService.validateCreateFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else {
      var res = postService.createPost(request.getTitle(), request.getContent());
      return ResponseBuilder.build(HttpStatus.CREATED.value(), MessageConst.POST_CREATED_SUCCESSFULLY, res);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updatePost(@PathVariable("id") String id, @RequestBody UpdatePostRequestDto request) {
    if (!postService.validateUpdateFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    } else if (!postService.validateUpdateFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else {
      var post = postService.getPostById(id);
      if (post == null) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.POST_NOT_FOUND);
      } else if (!post.getUserId().equals(authService.getAuthUser().getId())) {
        return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
      } else {
        var result = postService.updatePost(id, request.getTitle(), request.getContent());
        return ResponseBuilder.build(HttpStatus.OK.value(), MessageConst.POST_UPDATED_SUCCESSFULLY, result);
      }
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable("id") String id) {
    var post = postService.getPostById(id);
    if (post == null) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.POST_NOT_FOUND);
    } else if (!post.getUserId().equals(authService.getAuthUser().getId())) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    } else {
      postService.deletePost(id);
      return ResponseBuilder.build(HttpStatus.OK.value(), MessageConst.POST_DELETED_SUCCESSFULLY, null);
    }
  }
}
