package com.example.userpost.repository;

import com.example.userpost.model.post.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
  @Query("{'state': 1}")
  List<Post> findActivePost();

  @Query("{'userId': ?0, 'state': 1}")
  List<Post> findByUserId(String userId);

  @Query("{'title': {$regex: ?0, $options: 'i'}, 'state': 1}")
  List<Post> findByTitle(String title);

  @Query("{'title': {$regex: ?0, $options: 'i'}, 'userId': ?1, 'state': 1}")
  List<Post> findByTitleAndUserId(String title, String userId);
}
