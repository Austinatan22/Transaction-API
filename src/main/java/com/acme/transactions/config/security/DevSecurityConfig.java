package com.acme.transactions.config.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.core.context.SecurityContextHolder;



import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

  @Bean
  SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/actuator/**",
          "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"
        ).permitAll()
        .anyRequest().authenticated()
      )
      .addFilterBefore(xUserIdFilter(), AbstractPreAuthenticatedProcessingFilter.class)
      .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  Filter xUserIdFilter() {
    return new Filter() {
      @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) req;
        String userId = http.getHeader("X-User-Id");
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          Authentication auth = new UsernamePasswordAuthenticationToken(
              userId, "N/A", List.of(new SimpleGrantedAuthority("ROLE_USER")));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
      }
    };
  }
}
