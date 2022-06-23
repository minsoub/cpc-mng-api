package com.bithumbsystems.cpc.api.core.config.local;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.ses.SesClient;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
@Profile("local")
public class LocalAwsConfig {

    @Value("${cloud.aws.credentials.profile-name}")
    private String profileName;

    private final AwsProperties awsProperties;
    private KmsAsyncClient kmsAsyncClient;
    private final CredentialsProvider credentialsProvider;

    @Bean
    public S3AsyncClient s3client() {
        return S3AsyncClient.builder()
            .region(Region.of(awsProperties.getRegion()))
            .credentialsProvider(ProfileCredentialsProvider.create(profileName))
            .build();
    }

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
            .region(Region.of(awsProperties.getRegion()))
            .credentialsProvider(ProfileCredentialsProvider.create(profileName))
            .build();
    }

    @PostConstruct
    public void init() {
        kmsAsyncClient = KmsAsyncClient.builder()
            .region(Region.of(awsProperties.getRegion()))
            .credentialsProvider(ProfileCredentialsProvider.create(profileName))
            .build();
    }
}