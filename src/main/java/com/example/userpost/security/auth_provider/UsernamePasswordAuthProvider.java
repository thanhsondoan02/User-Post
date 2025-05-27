package com.example.userpost.security.auth_provider;

import com.example.userpost.constant.SecurityRole;
import com.example.userpost.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class
UsernamePasswordAuthProvider implements AuthenticationProvider {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UsernamePasswordAuthProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    var username = (String) authentication.getPrincipal();
    var password = (String) authentication.getCredentials();

    var user = userRepository.findActiveByUserName(username)
      .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new UsernameNotFoundException("Invalid password");
    }

    var role = switch (user.getRole()) {
      case USER -> SecurityRole.USER;
      case ADMIN -> SecurityRole.USER_ADMIN;
    };
    var authorities = List.of(new SimpleGrantedAuthority(role.withPrefix()));
    return new UsernamePasswordAuthenticationToken(user, null, authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}