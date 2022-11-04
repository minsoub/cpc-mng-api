package com.bithumbsystems.cpc.api.v1.education.service;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.exception.MailException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.model.enums.MailForm;
import com.bithumbsystems.cpc.api.core.util.AES256Util;
import com.bithumbsystems.cpc.api.core.util.MaskingUtil;
import com.bithumbsystems.cpc.api.core.util.message.MailSenderInfo;
import com.bithumbsystems.cpc.api.core.util.message.MessageService;
import com.bithumbsystems.cpc.api.v1.accesslog.request.AccessLogRequest;
import com.bithumbsystems.cpc.api.v1.education.mapper.EducationMapper;
import com.bithumbsystems.cpc.api.v1.education.model.request.EducationRequest;
import com.bithumbsystems.cpc.api.v1.education.model.response.EducationResponse;
import com.bithumbsystems.cpc.api.v1.protection.mapper.FraudReportMapper;
import com.bithumbsystems.persistence.mongodb.accesslog.model.enums.ActionType;
import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import com.bithumbsystems.persistence.mongodb.education.model.entity.Education;
import com.bithumbsystems.persistence.mongodb.education.service.EducationDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

    private EducationDomainService educationDomainService;
    private final AwsProperties awsProperties;

    private final MessageService messageService;
    private final SpringTemplateEngine templateEngine;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${webserver.url}")
    String webRootUrl;

    /**
     * 신청자 정보를 마스킹해서 리턴한다.
     *
     * @param startDate
     * @param endDate
     * @param isAnswerComplete
     * @param keyword
     * @return
     */
    public Flux<EducationResponse> searchList(LocalDate startDate, LocalDate endDate, Boolean isAnswerComplete, String keyword) {
        return educationDomainService.findBySearchText(startDate, endDate, keyword, isAnswerComplete)
                .map(result -> {
                    result.setName(MaskingUtil.getNameMask(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getName())));
                    result.setEmail(MaskingUtil.getEmailMask(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getEmail())));
                    result.setPhone(MaskingUtil.getPhoneMask(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getPhone())));

                    return result;
                })
                .map(EducationMapper.INSTANCE::educationResponse);
    }
    /**
     * 신청자 정보를 UnMasking 해서 리턴한다.
     *
     * @param startDate
     * @param endDate
     * @param isAnswerComplete
     * @param keyword
     * @param reasonContent
     * @param account
     * @return
     */
    public Flux<EducationResponse> searchListUnmasking(LocalDate startDate, LocalDate endDate, Boolean isAnswerComplete, String keyword, String reasonContent, Account account) {
        return educationDomainService.findBySearchText(startDate, endDate, keyword, isAnswerComplete)
                .map(result -> {
                    result.setName(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getName()));
                    result.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getEmail()));
                    result.setPhone(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getPhone()));

                    return result;
                })
                .map(EducationMapper.INSTANCE::educationResponse)
                .doOnComplete(() -> sendPrivacyAccessLog(ActionType.VIEW, reasonContent, account));
    }

    /**
     * 신청자 정보 상세 데이터를 마스킹 해서 리턴한다.
     *
     * @param id
     * @return
     */
    public Mono<EducationResponse> findById(String id) {
        return educationDomainService.findById(id)
                .map(result -> {
                    result.setName(MaskingUtil.getNameMask(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getName())));
                    result.setEmail(MaskingUtil.getEmailMask(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getEmail())));
                    result.setPhone(MaskingUtil.getPhoneMask(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getPhone())));

                    return result;
                })
                .map(EducationMapper.INSTANCE::educationResponse);
    }


    /**
     * 신청자 정보 상세 데이터를 Unmasking 해서 리턴한다.
     *
     * @param id
     * @param reason
     * @return
     */
    public Mono<EducationResponse> findByIdUnmasking(String id, String reason, Account account) {
        return educationDomainService.findById(id)
                .map(result -> {
                    result.setName(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getName()));
                    result.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getEmail()));
                    result.setPhone(AES256Util.decryptAES(awsProperties.getKmsKey(), result.getPhone()));

                    return result;
                })
                .map(EducationMapper.INSTANCE::educationResponse)
                .doOnSuccess(r -> sendPrivacyAccessLog(ActionType.VIEW, reason, account));
    }


    /**
     * 신청자 정보 저장.
     * @param educationRequest
     * @param account
     * @return
     */
    public Mono<EducationResponse> save(EducationRequest educationRequest, Account account) {

        return educationDomainService.findById(educationRequest.getId())
                .flatMap(result -> {
                    result.setAnswer(educationRequest.getAnswer());
                    result.setIsEmail(educationRequest.getIsEmail());
                    result.setUpdateAccountId(account.getAccountId());
                    result.setUpdateDate(LocalDateTime.now());
                    result.setIsAnswerComplete(true);
                    return educationDomainService.updateEducation(result)
                            .map(EducationMapper.INSTANCE::educationResponse)
                            .doOnSuccess(res -> {
                                if (educationRequest.getIsEmail()) {
                                    // send mail
                                    sendMail(
                                            AES256Util.decryptAES(awsProperties.getKmsKey(), res.getName()),
                                            AES256Util.decryptAES(awsProperties.getKmsKey(), res.getEmail()),
                                            educationRequest.getAnswer()
                                    );
                                }
                            });
                })
                .map(res -> {
                    if (educationRequest.getIsMasking()) {
                        res.setName(MaskingUtil.getNameMask(AES256Util.decryptAES(awsProperties.getKmsKey(), res.getName())));
                        res.setEmail(MaskingUtil.getEmailMask(AES256Util.decryptAES(awsProperties.getKmsKey(), res.getEmail())));
                        res.setPhone(MaskingUtil.getPhoneMask(AES256Util.decryptAES(awsProperties.getKmsKey(), res.getPhone())));
                    } else {
                        res.setName(AES256Util.decryptAES(awsProperties.getKmsKey(), res.getName()));
                        res.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), res.getEmail()));
                        res.setPhone(AES256Util.decryptAES(awsProperties.getKmsKey(), res.getPhone()));
                    }

                    return res;
                });
    }

    /**
     * 메일 전송
     * @param name
     * @param email
     * @param contents
     */
    private void sendMail(String name, String email, String contents) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("email", email);
            context.setVariable("contents", contents);
            context.setVariable("imgHeaderUrl", webRootUrl + "img/email/header.png");
            context.setVariable("imgFooterUrl", webRootUrl + "img/email/footer.png");

            String html = templateEngine.process("education-mail", context);
            log.info("mail address: {}", email);
            log.info("send mail: {}", html);

            messageService.send(
                    MailSenderInfo.builder()
                            .bodyHTML(html)
                            .subject(MailForm.EDUCATION_FORM.getSubject())
                            .emailAddress(email)
                            .build()
            );
        } catch (MessagingException | IOException e) {
            throw new MailException(ErrorCode.FAIL_SEND_MAIL);
        }
    }

    /**
     * 개인정보 접근 로그 발송
     * @param account
     */
    private void sendPrivacyAccessLog(ActionType actionType, String reason, Account account) {
        applicationEventPublisher.publishEvent(
                AccessLogRequest.builder()
                        .email(account.getEmail())
                        .accountId(account.getAccountId())
                        .ip(account.getUserIp())
                        .actionType(actionType)
                        .reason(reason)
                        .description("신청자 관리 - 개인정보 열람")
                        .siteId(account.getMySiteId())
                        .build()
        );
    }
}
