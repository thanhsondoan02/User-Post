package com.example.userpost.security;

import com.example.userpost.config.JwtConfig;
import com.example.userpost.constant.Role;
import com.example.userpost.constant.SecurityRole;
import com.example.userpost.repository.AcceptedConnectionRepository;
import com.example.userpost.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

  private final Key signingKey;
  private final long jwtExpiration;
  private final UserRepository userRepository;
  private final AcceptedConnectionRepository connectionRepository;
  private final PasswordEncoder passwordEncoder;

  public JwtUtils(
    JwtConfig jwtConfig,
    UserRepository userRepository,
    AcceptedConnectionRepository connectionRepository,
    PasswordEncoder passwordEncoder
  ) {
    this.signingKey = getSigningKey(jwtConfig.getSecret());
    this.jwtExpiration = jwtConfig.getExpiration();
    this.userRepository = userRepository;
    this.connectionRepository = connectionRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public String generateToken(String username, String password, Role role) {
    var securityRole = switch (role) {
      case USER -> SecurityRole.USER;
      case ADMIN -> SecurityRole.USER_ADMIN;
    };

    return Jwts.builder()
      .setSubject(username)
      .claim("role", securityRole)
      .claim("password", password)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
      .signWith(signingKey)
      .compact();
  }

  public String generateTokenOpenid(String clientId, String clientSecret) {
    return Jwts.builder()
      .setSubject(clientId)
      .claim("role", SecurityRole.CLIENT)
      .claim("clientSecret", clientSecret)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
      .signWith(signingKey)
      .compact();
  }

  public UsernamePasswordAuthenticationToken validateJwtToken(String token) {
    var claims = Jwts
      .parserBuilder()
      .setSigningKey(signingKey)
      .build()
      .parseClaimsJws(token)
      .getBody();

    var role = SecurityRole.fromString(claims.get("role", String.class));
    Object data;

    // With each role, validate the credentials and retrieve the corresponding data
    switch (role) {
      case USER, USER_ADMIN -> {
        var username = claims.getSubject();
        var password = claims.get("password", String.class);

        var user =  userRepository.findActiveByUserName(username)
          .orElseThrow(() -> new JwtException("Username not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
          throw new JwtException("Invalid password");
        }

        data = user;
      }
      case CLIENT -> {
        var clientId = claims.getSubject();
        var clientSecret = claims.get("clientSecret", String.class);

        var connection = connectionRepository.findActiveByClientId(clientId)
          .orElseThrow(() -> new JwtException("Client id not found"));

        if (!passwordEncoder.matches(clientSecret, connection.getClientSecret())) {
          throw new JwtException("Invalid client secret");
        }

        data = connection;
      }
      default -> throw new JwtException("Unknown login type");
    }

    var authorities = List.of(new SimpleGrantedAuthority(role.withPrefix()));
    return new UsernamePasswordAuthenticationToken(data, null, authorities);
  }

  private Key getSigningKey(String jwtSecret) {
    return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
  }
}

