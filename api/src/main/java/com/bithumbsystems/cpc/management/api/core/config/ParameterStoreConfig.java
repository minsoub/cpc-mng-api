package com.bithumbsystems.cpc.management.api.core.config;

import static com.bithumbsystems.cpc.management.api.core.config.constant.ParameterStoreConstant.DB_NAME;
import static com.bithumbsystems.cpc.management.api.core.config.constant.ParameterStoreConstant.DB_URL;
import static com.bithumbsystems.cpc.management.api.core.config.constant.ParameterStoreConstant.PASSWORD;
import static com.bithumbsystems.cpc.management.api.core.config.constant.ParameterStoreConstant.PORT;
import static com.bithumbsystems.cpc.management.api.core.config.constant.ParameterStoreConstant.USER;

import com.bithumbsystems.cpc.management.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.management.api.core.config.property.MongoProperties;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Log4j2
@Data
@Profile("dev|prod")
@Configuration
public class ParameterStoreConfig {

    private SsmClient ssmClient;
    private MongoProperties mongoProperties;

    private final AwsProperties awsProperties;

    @Value("${cloud.aws.credentials.profile-name}")
    private String profileName;

    @PostConstruct
    public void init() {

        log.debug("config store [prefix] => {}", awsProperties.getPrefix());
        log.debug("config store [name] => {}", awsProperties.getParamStoreName());

        this.ssmClient = SsmClient.builder()
            .region(Region.of(awsProperties.getRegion()))
            .build();

        this.mongoProperties = new MongoProperties(
            getParameterValue(DB_URL),
            getParameterValue(USER),
            getParameterValue(PASSWORD),
            getParameterValue(PORT),
            getParameterValue(DB_NAME)
        );
    }

    protected String getParameterValue(String type) {
        String parameterName = String.format("%s/%s_%s/%s", awsProperties.getPrefix(), awsProperties.getParamStoreName(), profileName, type);

        software.amazon.awssdk.services.ssm.model.GetParameterRequest request = GetParameterRequest.builder()
            .name(parameterName)
            .withDecryption(true)
            .build();

        GetParameterResponse response = this.ssmClient.getParameter(request);

        return response.parameter().value();
    }
}