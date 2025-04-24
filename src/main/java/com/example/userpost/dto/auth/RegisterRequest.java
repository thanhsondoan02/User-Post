package com.example.userpost.dto.auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
  private String username;
  private String email;
  private String password;
  private String fullName;
  private String gender;
  private LocalDate dateOfBirth;
}

