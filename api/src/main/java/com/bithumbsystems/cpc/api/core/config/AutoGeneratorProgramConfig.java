package com.bithumbsystems.cpc.api.core.config;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.cpc.api.core.config.property.ApplicationProperties;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoGeneratorProgramConfig {

  private final RequestMappingHandlerMapping requestMappingHandlerMapping;
  private final ReactiveMongoTemplate reactiveMongoTemplate;

  private final ApplicationProperties applicationProperties;
  private static final String ACCOUNT_ID = "autoAccountId";
  private static final String PROGRAM_PREFIX = "PROGRAM_";

  public enum RoleType {
    USER, ADMIN, ANONYMOUS
  }

  public enum ActionMethod {
    GET, POST, DELETE, PUT
  }

  @EventListener(ContextRefreshedEvent.class)
  public void start() {
    Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
    map.entrySet()
        .stream()
        .filter(e -> e.getValue().getMethodAnnotation(Operation.class) != null)
        .filter(e -> {
          final var url = e.getKey().getPatternsCondition().getPatterns().iterator().next()
              .getPatternString();
          return !url.contains("api-docs") && !url.contains("swagger-ui");
        }).map(e -> {
          var operation = e.getValue().getMethodAnnotation(Operation.class);
          return Program.builder()
              .name(Objects.requireNonNull(operation).summary())
              .type(RoleType.valueOf(applicationProperties.getRoleType()))
              .kindName(operation.tags() != null ? operation.tags()[0] : null)
              .actionMethod(ActionMethod.valueOf(
                  e.getKey().getMethodsCondition().getMethods().iterator().next().name()))
              .actionUrl(e.getKey().getPatternsCondition().getPatterns().iterator().next()
                  .getPatternString())
              .isUse(false)
              .description(operation.description())
              .build();
        })
        .map(
            program -> existsRegisterUrls(program.getActionMethod().name(),
                program.getActionUrl())
                .filter(exists -> !exists)
                .map(exist -> {
                  program.setSiteId(applicationProperties.getSiteId());
                  program.setCreateDate(LocalDateTime.now());
                  program.setCreateAdminAccountId(ACCOUNT_ID);
                  program.setId(PROGRAM_PREFIX + generateUUIDWithOutDash());
                  return program;
                }).flatMap(p -> {
                  log.debug("register program : {}" , p.toString());
                  return reactiveMongoTemplate.save(program);
                }).subscribe()
        ).close();
  }

  public Mono<Boolean> existsRegisterUrls(String method, String path) {
    Query query = new Query();
    query.addCriteria(new Criteria().andOperator(where("action_method").is(method), where("action_url").is(path)));
    return reactiveMongoTemplate.exists(query, Program.class);
  }

  private String generateUUIDWithOutDash() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  @Document(collection = "program")
  @AllArgsConstructor
  @Getter
  @Setter
  @Builder
  @ToString
  @NoArgsConstructor
  public static class Program {
    @MongoId(targetType = FieldType.STRING)
    private String id;
    private String name;
    private RoleType type;
    private String kindName;
    private ActionMethod actionMethod;
    private String actionUrl;
    private Boolean isUse;
    private String description;
    @Indexed
    private String siteId;
    private LocalDateTime createDate;
    private String createAdminAccountId;
  }
}