package com.bithumbsystems.cpc.api.v1.accesslog.listener;

import com.bithumbsystems.cpc.api.v1.accesslog.request.AccessLogRequest;
import com.bithumbsystems.persistence.mongodb.accesslog.model.entity.AccessLog;
import com.bithumbsystems.persistence.mongodb.accesslog.service.AccessLogDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Usage
 * 선언 : private final ApplicationEventPublisher applicationEventPublisher;
 * 데이터 전송
 *         applicationEventPublisher.publishEvent(
 *                 AccessLogRequest.builder()
 *                         .email(account.getEmail())
 *                         .accountId(account.getAccountId())
 *                         .ip(account.getUserIp())
 *                         .actionType(ActionType.VIEW)
 *                         .reason('다운로드 사유 입력')
 *                         .description('간략한 업무명')
 *                         .build()
 *         );
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AccessLogListener {
    private final AccessLogDomainService accessLogDomainService;

    @EventListener
    public void accessLog(AccessLogRequest accessLogRequest) {

        accessLogDomainService.insert(AccessLog.builder()
                .id(UUID.randomUUID().toString())
                .account_id(accessLogRequest.getAccountId())
                .email(accessLogRequest.getEmail())
                .action_type(accessLogRequest.getActionType())
                .reason(accessLogRequest.getReason())
                .description(accessLogRequest.getDescription())
                .create_date(LocalDateTime.now())
                .build()).publishOn(Schedulers.boundedElastic()).subscribe();
    }
}
