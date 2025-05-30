package com.example.userpost.controller;

import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.MessageConst;
import com.example.userpost.constant.SecurityRole;
import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.dto.request.post.UpdatePostRequestDto;
import com.example.userpost.dto.response.post.PostListResponseDto;
import com.example.userpost.dto.response.post.PostResponseDto;
import com.example.userpost.model.post.Post;
import com.example.userpost.service.IApiService;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IPostService;
import com.example.userpost.service.IUserService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final IPostService postService;
  private final IAuthService authService;
  private final IUserService userService;
  private final IApiService apiService;

  public PostController(IPostService postService, IAuthService authService, IUserService userService, IApiService apiService) {
    this.postService = postService;
    this.authService = authService;
    this.userService = userService;
    this.apiService = apiService;
  }

  @PostMapping
  public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto request) {
    if (!postService.validateCreateFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    } else if (!postService.validateCreateFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else {
      var res = postService.createPost(request.getTitle(), request.getContent());

      // Send webhooks
      apiService.sendWebhookScopePosts(HookEvent.CREATE, res);

      return ResponseBuilder.build(HttpStatus.CREATED.value(), MessageConst.POST_CREATED_SUCCESSFULLY, res);
    }
  }

  @GetMapping
  public ResponseEntity<?> searchPost(@RequestParam(required = false) String title, @RequestParam(required = false) String userId) {
    if (authService.getAuthRole() == SecurityRole.CLIENT) {
      return ResponseBuilder.success(new PostListResponseDto(postService.getAll()));
    } else {
      List<Post> posts;
      if (title == null && userId == null) {
        posts = postService.getAll();
      } else if (userId == null) { // titleId != null
        posts = postService.searchPostByTitle(title);
      } else { // userId != null
        if (!userService.isUserIdExist(userId)) {
          return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.USER_NOT_FOUND);
        }
        if (title == null) {
          posts = postService.searchPostByUser(userId);
        } else {
          posts = postService.searchPostByTitleAndUser(title, userId);
        }
      }
      return ResponseBuilder.success(new PostListResponseDto(posts));
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

        // Send webhooks
        apiService.sendWebhookScopePosts(HookEvent.UPDATE, result);

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

      // Send webhooks
      var dto = new PostResponseDto();
      dto.setId(id);
      apiService.sendWebhookScopePosts(HookEvent.DELETE, dto);

      return ResponseBuilder.build(HttpStatus.OK.value(), MessageConst.POST_DELETED_SUCCESSFULLY, null);
    }
  }
}
