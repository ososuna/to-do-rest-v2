package com.todo.todoapi.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION = "Authorization";
  private static final String APPLICATION_JSON_VALUE = "application/json";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    log.info("do filter");
    if (
      request.getServletPath().equals("/auth/login")
        || request.getServletPath().equals("/auth/register")
        || request.getServletPath().equals("/auth/token/refresh")
    ) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorizationHeader = request.getHeader(AUTHORIZATION);
    
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring("Bearer ".length());
      if (token != null && token.length() > 0) {
        try {
          Algorithm algorithm = Algorithm.HMAC256("T0dOAP1v3rSion2".getBytes());
          JWTVerifier verifier = JWT.require(algorithm).build();
          DecodedJWT decodedJWT = verifier.verify(token);
          String email = decodedJWT.getSubject();
          String role = decodedJWT.getClaim("roles").asString();
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
          authorities.add(new SimpleGrantedAuthority(role));
          UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          filterChain.doFilter(request, response);
          return;
        } catch (Exception e) {
          log.error("Error logging in: {}", e.getMessage());
          response.setHeader("error", e.getMessage());
          response.setStatus(HttpStatus.FORBIDDEN.value());
          Map<String, String> error = new HashMap<>();
          error.put("message", e.getMessage());
          response.setContentType(APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), error);
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
      }
    } else {
      filterChain.doFilter(request, response);
      return;
    }
  }
}
