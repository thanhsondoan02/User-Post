package com.example.userpost.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtils {

  private static final Pattern EMAIL_REGEX = Pattern.compile(
    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
    Pattern.CASE_INSENSITIVE
  );

  private static final Pattern PASSWORD_REGEX = Pattern.compile(
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,20}$"
  );

  private ValidationUtils() {}

  public static boolean isEmailValid(String email) {
    return email != null && EMAIL_REGEX.matcher(email).matches();
  }

  public static boolean isPasswordValid(String password) {
    return password != null && PASSWORD_REGEX.matcher(password).matches();
  }

  public static boolean isUsernameValid(String username) {
    return username != null && username.length() >= 6 && username.length() <= 20;
  }

  public static boolean isGenderValid(String gender) {
    return gender != null && (gender.equals("M") || gender.equals("F"));
  }

  public static boolean isDateOfBirthValid(LocalDate dateOfBirth) {
    return dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now());
  }
}

