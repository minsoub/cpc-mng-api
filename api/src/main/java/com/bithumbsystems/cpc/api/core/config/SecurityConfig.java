package com.bithumbsystems.cpc.api.core.config;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.exception.InvalidTokenException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AwsProperties awsProperties;

  public ReactiveJwtDecoder reactiveJwtDecoder() {
    SecretKeySpec secretKey = new SecretKeySpec(awsProperties.getJwtSecretKey().getBytes(), MacAlgorithm.HS512.getName());

    return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS512)
        .build();
  }

  @Bean
  SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
    return http
        .authorizeExchange(exchanges -> exchanges
            .anyExchange().permitAll()
        )
        .csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .oauth2ResourceServer().jwt(jwtSpec -> {
          try {
            jwtSpec.authenticationManager(jwtReactiveAuthenticationManager(reactiveJwtDecoder()));
          } catch (Exception e) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
          }
        }).and().build();
  }

  public ReactiveAuthenticationManager jwtReactiveAuthenticationManager(ReactiveJwtDecoder reactiveJwtDecoder) {
    return new JwtReactiveAuthenticationManager(reactiveJwtDecoder);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
