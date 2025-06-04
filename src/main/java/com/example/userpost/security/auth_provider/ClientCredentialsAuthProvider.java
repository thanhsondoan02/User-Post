package com.example.userpost.security.auth_provider;

import com.example.userpost.constant.ConnectionStatus;
import com.example.userpost.constant.SecurityRole;
import com.example.userpost.repository.ConnectionRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientCredentialsAuthProvider implements AuthenticationProvider {
  private final ConnectionRepository connectionRepository;
  private final PasswordEncoder passwordEncoder;

  public ClientCredentialsAuthProvider(ConnectionRepository connectionRepository, PasswordEncoder passwordEncoder) {
    this.connectionRepository = connectionRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    var clientId = (String) authentication.getPrincipal();
    var clientSecret = (String) authentication.getCredentials();

    var connection = connectionRepository.findActiveByClientId(clientId)
      .orElseThrow(() -> new BadCredentialsException("Client id not found"));

    if (connection.getStatus() != ConnectionStatus.ACCEPTED) {
      throw new BadCredentialsException("Connection is not accepted");
    }

    if (!passwordEncoder.matches(clientSecret, connection.getClientSecret())) {
      throw new BadCredentialsException("Invalid client secret");
    }

    var authorities = List.of(new SimpleGrantedAuthority(SecurityRole.CLIENT.withPrefix()));
    return new UsernamePasswordAuthenticationToken(connection, null, authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}