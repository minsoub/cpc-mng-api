package com.bithumbsystems.cpc.api.core.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Slf4j
@Configuration
@Profile("dev|qa|prod|eks-dev|eks-prod")
@RequiredArgsConstructor
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    private final ParameterStoreConfig config;

    @Override
    protected String getDatabaseName() {
        return config.getMongoProperties().getMongodbName();
    }

    @Override
    public MongoClient reactiveMongoClient() {
        log.info("Applying AWS DocumentDB Configuration");
        MongoClientSettings settings = configureClientSettings();
        return MongoClients.create(settings);
    }

    @SneakyThrows
    protected MongoClientSettings configureClientSettings() {
        return MongoClientSettings.builder()
            .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
            .applyConnectionString(getConnectionString())
            .build();
    }
    private ConnectionString getConnectionString() {
        String str = String.format("mongodb://%s:%s@%s:%s",
            config.getMongoProperties().getMongodbUser(),
            config.getMongoProperties().getMongodbPassword(),
            config.getMongoProperties().getMongodbUrl(),
            config.getMongoProperties().getMongodbPort()
        );

        return new ConnectionString(str);
    }

    @Bean
    public ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }

    @Bean
    public TransactionalOperator transactionOperator(ReactiveTransactionManager manager) {
        return TransactionalOperator.create(manager);
    }
    @Bean
    @Primary
    @Override
    public MappingMongoConverter mappingMongoConverter(ReactiveMongoDatabaseFactory databaseFactory,
        MongoCustomConversions customConversions, MongoMappingContext mappingContext) {
        mappingContext.setFieldNamingStrategy(new SnakeCaseFieldNamingStrategy());

        MappingMongoConverter converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
        converter.setCustomConversions(customConversions);
        converter.setCodecRegistryProvider(databaseFactory);

        return converter;
    }
}