package com.bithumbsystems.cpc.api.core.config;

import com.bithumbsystems.cpc.api.core.config.property.ApplicationProperties;
import com.bithumbsystems.cpc.api.core.config.resolver.CustomArgumentResolver;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.core.jackson.ModelResolver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@EnableWebFlux
@Slf4j
@RequiredArgsConstructor
public class WebFluxConfig implements WebFluxConfigurer {

  private final ApplicationProperties applicationProperties;
  private final ReactiveJwtDecoder reactiveJwtDecoder;

  @Override
  public void configurePathMatching(PathMatchConfigurer configurer) {
    configurer.addPathPrefix( applicationProperties.getPrefix() + applicationProperties.getVersion() + applicationProperties.getProject()
        , (path) -> Arrays
            .stream(applicationProperties.getExcludePrefixPath())
            .anyMatch(p -> !(path.getName().indexOf(p) > 0))
    );
  }

  @Override
  public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper()));
    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper()));
    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024);
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    JavaTimeModule module = new JavaTimeModule();
    LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    module.addSerializer(LocalDateTime.class, localDateTimeSerializer);
    module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);

    SimpleModule simpleModule = new SimpleModule();
    StringDeserializer stringDeserializer = new StringDeserializer();
    simpleModule.addDeserializer(String.class, stringDeserializer);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(module);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // snake case 로 변환
    return objectMapper;
  }

  @Bean
  public ModelResolver modelResolver(ObjectMapper objectMapper) {
    return new ModelResolver(objectMapper);
  }

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    WebFluxConfigurer.super.configureArgumentResolvers(configurer);
    CustomArgumentResolver customArgumentResolver = new CustomArgumentResolver(reactiveJwtDecoder);
    configurer.addCustomResolver(customArgumentResolver);
  }
}
