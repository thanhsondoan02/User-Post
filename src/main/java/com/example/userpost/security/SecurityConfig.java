package com.example.userpost.security;

import com.example.userpost.constant.SecurityRole;
import com.example.userpost.security.auth_provider.ClientCredentialsAuthProvider;
import com.example.userpost.security.auth_provider.UsernamePasswordAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final UsernamePasswordAuthProvider usernamePasswordAuthProvider;
  private final ClientCredentialsAuthProvider clientCredentialsAuthProvider;

  public SecurityConfig(
    JwtAuthenticationFilter jwtAuthenticationFilter,
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
    UsernamePasswordAuthProvider usernamePasswordAuthProvider,
    ClientCredentialsAuthProvider clientCredentialsAuthProvider
  ) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.usernamePasswordAuthProvider = usernamePasswordAuthProvider;
    this.clientCredentialsAuthProvider = clientCredentialsAuthProvider;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    var userRole = SecurityRole.USER.toString();
    var adminRole = SecurityRole.USER_ADMIN.toString();
    var clientRole = SecurityRole.CLIENT.toString();

    http
      .csrf(AbstractHttpConfigurer::disable)
      .exceptionHandling(e ->
        e.authenticationEntryPoint(customAuthenticationEntryPoint)
      )
      .authorizeHttpRequests(auth -> auth
        // Public endpoints
        .requestMatchers(
          "/api/auth/register", "/api/auth/login",
          "/api/auth/login-openid", "api/auth/refresh", "api/auth/refresh-openid"
        ).permitAll()
        .requestMatchers(HttpMethod.POST, "/api/connections").permitAll() // Create a new connection

        // Admin-only endpoints
        .requestMatchers(HttpMethod.GET, "/api/connections").hasRole(adminRole) // View connections
        .requestMatchers("/api/connections/{id}").hasRole(adminRole) // Accept or reject connection
        .requestMatchers(HttpMethod.POST, "/api/scopes").hasRole(adminRole) // Create a new scope
        .requestMatchers(HttpMethod.POST, "/api/events").hasRole(adminRole) // Create a new event
        .requestMatchers(HttpMethod.POST, "/api/event_scopes").hasRole(adminRole) // Map scopes to events

        // Client or admin endpoints
        .requestMatchers(HttpMethod.GET, "/api/events").hasAnyRole(clientRole, adminRole) // View all events

        // Client-only endpoints
        .requestMatchers("/api/webhooks").hasRole(clientRole)
        .requestMatchers("/api/webhooks/{id}").hasRole(clientRole)

        // Client or user endpoints
        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole(userRole, adminRole, clientRole)
        .requestMatchers(HttpMethod.GET, "/api/posts").hasAnyRole(userRole, adminRole, clientRole)
        .requestMatchers(HttpMethod.GET, "/api/groups").hasAnyRole(userRole, adminRole, clientRole)

        // Other requests: required user or admin role
        .anyRequest().hasAnyRole(userRole, adminRole)
      )
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      );

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Arrays.asList(
      usernamePasswordAuthProvider,
      clientCredentialsAuthProvider
    ));
  }
}

