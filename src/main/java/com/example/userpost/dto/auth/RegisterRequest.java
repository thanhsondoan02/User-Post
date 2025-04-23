package com.example.userpost.dto.auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
  private String fullName;
  private String gender;
  private LocalDate dateOfBirth;
}

