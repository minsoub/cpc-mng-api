package com.bithumbsystems.cpc.api.core.config.resolver;

import com.bithumbsystems.cpc.api.core.exception.InvalidTokenException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.nimbusds.jose.shaded.json.JSONArray;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class CustomArgumentResolver implements HandlerMethodArgumentResolver {

  private final ReactiveJwtDecoder reactiveJwtDecoder;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentUser.class);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext,
      ServerWebExchange exchange) {
    var token = "";
    final String AUTHORIZATION = "Authorization";
    final String BEARER_TYPE = "Bearer";
    final String MY_SITE_ID = "my_site_id";
    final String USER_IP = "user_ip";

    for (String value : Objects.requireNonNull(
        exchange.getRequest().getHeaders().get(AUTHORIZATION))) {
      if (value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
        token = value.substring(BEARER_TYPE.length()).trim();
      }
    }
    log.debug(token);
    String mySiteId = exchange.getRequest().getHeaders().get(MY_SITE_ID) != null ?
        Objects.requireNonNull(exchange.getRequest().getHeaders().get(MY_SITE_ID)).get(0) : "";
    String userIp = exchange.getRequest().getHeaders().get(USER_IP) != null ?
        Objects.requireNonNull(exchange.getRequest().getHeaders().get(USER_IP)).get(0) : "";
    return reactiveJwtDecoder.decode(token)
        .flatMap(jwt -> {
          log.debug("Role => {}", jwt.getClaimAsString("ROLE"));
          if (StringUtils.isAnyEmpty(jwt.getClaimAsString(
              "account_id"))) {
            return Mono.error(new InvalidTokenException(ErrorCode.INVALID_TOKEN));
          }
          final var accountId = jwt.getClaimAsString("account_id");
          final var roles = (JSONArray) jwt.getClaim("ROLE");
          final var email = jwt.getClaimAsString("user_id");
          return Mono.just(new Account(accountId,
              roles.stream().map(Object::toString).collect(Collectors.toSet()), email, userIp,
              mySiteId));
        });
  }
}
