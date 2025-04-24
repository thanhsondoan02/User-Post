package com.example.userpost.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
  private String username;
  private String email;
  private String password;
  private String fullName;
  private String gender;
  private LocalDate dateOfBirth;
}

