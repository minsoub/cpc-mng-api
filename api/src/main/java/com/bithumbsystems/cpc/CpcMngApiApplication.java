package com.bithumbsystems.cpc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication(scanBasePackages = "com.bithumbsystems", exclude = {
    MongoAutoConfiguration.class,
    MongoReactiveAutoConfiguration.class,
    MongoDataAutoConfiguration.class,
    EmbeddedMongoAutoConfiguration.class
})
@EnableWebFlux
@ConfigurationPropertiesScan("com.bithumbsystems.cpc.api.core.config")
@EnableReactiveMongoRepositories("com.bithumbsystems.persistence.mongodb")
@OpenAPIDefinition(info = @Info(title = "Customer Protection Center Management API", version = "1.0", description = "Customer Protection Center Management APIs v1.0"))
public class CpcMngApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(CpcMngApiApplication.class, args);
  }
}
