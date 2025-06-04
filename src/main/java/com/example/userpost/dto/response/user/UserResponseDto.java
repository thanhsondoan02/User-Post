package com.example.userpost.dto.response.user;

import com.example.userpost.constant.Role;
import com.example.userpost.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
  private String id;
  private String username;
  private String email;
  private String fullName;
  private String gender;
  private String role;
  private Long dateOfBirth;

  public UserResponseDto(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.fullName = user.getFullName();
    if (user.getGender() != null) {
      this.gender = user.getGender().name();
    }
    this.dateOfBirth = user.getDateOfBirth();
    if (user.getRole() == Role.ADMIN) {
      this.role = user.getRole().toString();
    }
  }

  public static UserResponseDto cloneFromUser(User user) {
    var dto = new UserResponseDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setFullName(user.getFullName());
    if (user.getGender() != null) {
      dto.setGender(user.getGender().toString());
    }
    if (user.getRole() != null) {
      dto.setRole(user.getRole().toString());
    }
    dto.setDateOfBirth(user.getDateOfBirth());
    return dto;
  }
}

