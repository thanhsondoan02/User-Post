package com.example.userpost.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserDto {
  private String id;
  private String username;
  private String email;
  private String fullName;
  private String gender;
  private LocalDate dateOfBirth;
}

