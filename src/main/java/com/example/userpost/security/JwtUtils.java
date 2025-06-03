package com.example.userpost.security;

import com.example.userpost.config.JwtConfig;
import com.example.userpost.constant.SecurityRole;
import com.example.userpost.model.openid.Server;
import com.example.userpost.model.token.ServerToken;
import com.example.userpost.model.token.UserToken;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.ServerTokenRepository;
import com.example.userpost.repository.UserTokenRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
  private final Key signingKey;
  private final SecureRandom secureRandom = new SecureRandom();

  private final JwtConfig jwtConfig;
  private final UserTokenRepository userTokenRepository;
  private final ServerTokenRepository serverTokenRepository;

  public JwtUtils(
    JwtConfig jwtConfig,
    UserTokenRepository userTokenRepository,
    ServerTokenRepository serverTokenRepository
  ) {
    this.jwtConfig = jwtConfig;
    this.userTokenRepository = userTokenRepository;
    this.serverTokenRepository = serverTokenRepository;

    this.signingKey = getSigningKey(jwtConfig.getSecret());
  }

  public UserToken genRefreshAndAccessToken(User user) {
    var refreshExpiredAt = System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration();
    var accessExpiredAt = System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration();
    var userToken = new UserToken(
      user,
      generateRefreshToken(),
      refreshExpiredAt,
      generateAccessToken(user, accessExpiredAt),
      accessExpiredAt
    );
    return userTokenRepository.save(userToken);
  }

  public ServerToken genRefreshAndAccessToken(Server server) {
    var refreshExpiredAt = System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration();
    var accessExpiredAt = System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration();
    var userToken = new ServerToken(
      server,
      generateRefreshToken(),
      refreshExpiredAt,
      generateAccessToken(server, accessExpiredAt),
      accessExpiredAt
    );
    return serverTokenRepository.save(userToken);
  }

  public UserToken refreshTokenUser(String refreshToken) {
    var userToken = userTokenRepository
      .findByRefreshToken(refreshToken, System.currentTimeMillis())
      .orElseThrow(() -> new BadCredentialsException("Invalid user refresh token"));
    var expiredAt = System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration();
    userToken.setAccessToken(generateAccessToken(userToken.getUser(), expiredAt));
    userToken.setAccessExpiredAt(expiredAt);
    return userTokenRepository.save(userToken);
  }

  public ServerToken refreshTokenServer(String refreshToken) {
    var serverToken = serverTokenRepository
      .findByRefreshToken(refreshToken, System.currentTimeMillis())
      .orElseThrow(() -> new BadCredentialsException("Invalid server refresh token"));
    var expiredAt = System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration();
    serverToken.setAccessToken(generateAccessToken(serverToken.getServer(), expiredAt));
    serverToken.setAccessExpiredAt(expiredAt);
    return serverTokenRepository.save(serverToken);
  }

  private String generateRefreshToken() {
    // Create random bytes (32 bytes = 256 bits)
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);

    // Convert to Base64 URL-safe string
    return Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(randomBytes);
  }

  private String generateAccessToken(User user, Long expiredAt) {
    var securityRole = switch (user.getRole()) {
      case USER -> SecurityRole.USER;
      case ADMIN -> SecurityRole.USER_ADMIN;
    };

    return Jwts.builder()
      .setSubject(user.getId())
      .claim("role", securityRole)
      .setIssuedAt(new Date())
      .setExpiration(new Date(expiredAt))
      .signWith(signingKey)
      .compact();
  }

  private String generateAccessToken(Server server, Long expiredAt) {
    return Jwts.builder()
      .setSubject(server.getId())
      .claim("role", SecurityRole.CLIENT)
      .setIssuedAt(new Date())
      .setExpiration(new Date(expiredAt))
      .signWith(signingKey)
      .compact();
  }

  public UsernamePasswordAuthenticationToken validateAccessToken(String token) {
    var claims = Jwts
      .parserBuilder()
      .setSigningKey(signingKey)
      .build()
      .parseClaimsJws(token)
      .getBody();

    var role = SecurityRole.fromString(claims.get("role", String.class));
    Object data;

    data = switch (role) {
      case USER, USER_ADMIN -> userTokenRepository.findUserByAccessToken(token, System.currentTimeMillis())
        .orElseThrow(() -> new JwtException("Invalid user access token"));
      case CLIENT -> serverTokenRepository.findServerByAccessToken(token, System.currentTimeMillis())
        .orElseThrow(() -> new JwtException("Invalid server access token"));
    };

    var authorities = List.of(new SimpleGrantedAuthority(role.withPrefix()));
    return new UsernamePasswordAuthenticationToken(data, null, authorities);
  }

  private Key getSigningKey(String jwtSecret) {
    return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
  }
}

