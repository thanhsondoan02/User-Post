package com.example.userpost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class UserPostApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserPostApplication.class, args);
    }

}
