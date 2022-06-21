package com.bithumbsystems.cpc.api.core.config;

import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.DB_NAME;
import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.DB_PASSWORD;
import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.DB_PORT;
import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.DB_URL;
import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.DB_USER;
import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.KMS_ALIAS_NAME;
import static com.bithumbsystems.cpc.api.core.config.constant.ParameterStoreConstant.MAIL_SENDER;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.config.property.MongoProperties;
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
@Profile("dev|prod|eks-dev")
@Configuration
public class ParameterStoreConfig {

    private SsmClient ssmClient;
    private MongoProperties mongoProperties;

    private final AwsProperties awsProperties;

    private final AwsConfig awsConfig;

    @Value("${cloud.aws.credentials.profile-name}")
    private String profileName;

    @PostConstruct
    public void init() {

        log.debug("config store [prefix] => {}", awsProperties.getPrefix());
        log.debug("config store [name] => {}", awsProperties.getParamStoreDocName());

        this.ssmClient = SsmClient.builder()
            .region(Region.of(awsProperties.getRegion()))
            .build();

        this.mongoProperties = new MongoProperties(
            getParameterValue(awsProperties.getParamStoreDocName(), DB_URL),
            getParameterValue(awsProperties.getParamStoreDocName(), DB_USER),
            getParameterValue(awsProperties.getParamStoreDocName(), DB_PASSWORD),
            getParameterValue(awsProperties.getParamStoreDocName(), DB_PORT),
            getParameterValue(awsProperties.getParamStoreDocName(), DB_NAME)
        );

        // KMS Parameter Key
        this.awsProperties.setKmsKey(getParameterValue(awsProperties.getParamStoreKmsName(), KMS_ALIAS_NAME));
        this.awsProperties.setEmailSender(getParameterValue(awsProperties.getParamStoreMessageName(), MAIL_SENDER));
    }

    protected String getParameterValue(String storeName, String type) {
        String parameterName = String.format("%s/%s_%s/%s", awsProperties.getPrefix(), storeName, profileName, type);

        GetParameterRequest request = GetParameterRequest.builder()
            .name(parameterName)
            .withDecryption(true)
            .build();

        GetParameterResponse response = this.ssmClient.getParameter(request);

        return response.parameter().value();
    }
}