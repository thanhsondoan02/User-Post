package com.example.userpost.controller;

import com.example.userpost.constant.MessageConst;
import com.example.userpost.dto.request.post.CreatePostRequestDto;
import com.example.userpost.service.IPostService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  IPostService postService;

  public PostController(IPostService postService) {
    this.postService = postService;
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
}
