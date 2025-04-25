package com.example.userpost.model.base;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseMongoEntity implements Serializable {

  @Id
  private String id;

  @Field("created_at")
  @CreatedDate
  private Instant createdAt;

  @Field("updated_at")
  @LastModifiedDate
  private Instant updatedAt;

  @Field("state")
  @Builder.Default
  @Indexed
  private State state = State.ACTIVE;
}
