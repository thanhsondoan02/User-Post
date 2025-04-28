package com.example.userpost.config;

import com.example.userpost.model.base.StateReadConverter;
import com.example.userpost.model.base.StateWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

  @Bean
  public MongoCustomConversions customConversions() {
    return new MongoCustomConversions(List.of(
      new StateReadConverter(),
      new StateWriteConverter()
    ));
  }
}
