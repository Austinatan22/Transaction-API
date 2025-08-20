package com.acme.transactions.config.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@Profile("prod")
public class ProdSecurityConfig {

  @Bean
  SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/actuator/**",
          "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"
        ).permitAll()
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth -> oauth
        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
      );
    return http.build();
  }


  private JwtAuthenticationConverter jwtAuthConverter() {
    var granted = new JwtGrantedAuthoritiesConverter();
    granted.setAuthorityPrefix("SCOPE_");
    granted.setAuthoritiesClaimName("scope");

    var conv = new JwtAuthenticationConverter();
    conv.setJwtGrantedAuthoritiesConverter(granted);
    return conv;
  }

}
