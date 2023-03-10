package com.bithumbsystems.cpc.api.core.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class AwsProperties {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.s3.board-bucket}")
  private String boardBucket;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Value("${cloud.aws.ssm.endpoint}")
  private String ssmEndPoint;

  @Value("${cloud.aws.ses.endpoint}")
  private String sesEndPoint;

  @Value("${cloud.aws.kms.endpoint}")
  private String kmsEndPoint;

  @Value("${cloud.aws.param-store.prefix}")
  private String prefix;

  @Value("${cloud.aws.param-store.doc-name}")
  private String paramStoreDocName;

  @Value("${cloud.aws.param-store.kms-name}")
  private String paramStoreKmsName;
  @Value("${cloud.aws.param-store.salt-name}")
  private String paramStoreSaltName;
  @Value("${cloud.aws.param-store.iv-name}")
  private String paramStoreIvName;
  @Value("${cloud.aws.param-store.message-name}")
  private String paramStoreMessageName;

  @Value("${cloud.aws.param-store.auth-name}")
  private String paramStoreAuthName;

  private String kmsKey;
  private String saltKey;
  private String ivKey;
  @Value("${cloud.aws.ses.port}")
  private String smtpPort;
  @Setter
  private String emailSender;
  @Setter
  private String smtpUserName;
  @Setter
  private String smtpUserPassword;
  private String jwtSecretKey;

}
