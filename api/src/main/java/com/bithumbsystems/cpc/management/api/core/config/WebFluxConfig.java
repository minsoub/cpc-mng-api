package com.bithumbsystems.cpc.management.api.core.config;

import com.bithumbsystems.cpc.management.api.core.config.property.ApplicationProperties;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
@Slf4j
@RequiredArgsConstructor
public class WebFluxConfig implements WebFluxConfigurer {

  private final ApplicationProperties applicationProperties;

  @Override
  public void configurePathMatching(PathMatchConfigurer configurer) {
    configurer.addPathPrefix( applicationProperties.getPrefix() + applicationProperties.getVersion()
        , (path) -> Arrays
            .stream(applicationProperties.getExcludePrefixPath())
            .anyMatch(p -> !(path.getName().indexOf(p) > 0))
    );
  }
}
