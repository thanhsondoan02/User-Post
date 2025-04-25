package com.example.userpost.model.post;

import com.example.userpost.model.base.BaseMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "posts")
public class Post extends BaseMongoEntity {

  @Field("title")
  private String title;

  @Field("content")
  private String content;

  @Field("user_id")
  @Indexed
  private String userId;
}
