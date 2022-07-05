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

    for (String value : Objects.requireNonNull(
        exchange.getRequest().getHeaders().get(AUTHORIZATION))) {
      if (value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
        token = value.substring(BEARER_TYPE.length()).trim();
      }
    }
    log.debug(token);
    return reactiveJwtDecoder.decode(token)
        .flatMap((jwt) -> {
            log.debug("Role => {}", jwt.getClaimAsString("ROLE"));
            // TODO: getSubject 없음, isAnyEmpty는 jsonArray 처리 못함.
          if(StringUtils.isAnyEmpty(jwt.getClaimAsString("account_id") )) { // , jwt.getClaim("ROLE"))) {  // jwt.getSubject(), jwt.getClaimAsString("account_id"), jwt.getClaim("ROLE"))) {
            return Mono.error(new InvalidTokenException(ErrorCode.INVALID_TOKEN));
          }
          final var accountId = jwt.getClaimAsString("account_id");
          final var roles = (JSONArray)jwt.getClaim("ROLE");
          return Mono.just(new Account(accountId, roles.stream().map(Object::toString).collect(Collectors.toSet())));
        });
  }
}
