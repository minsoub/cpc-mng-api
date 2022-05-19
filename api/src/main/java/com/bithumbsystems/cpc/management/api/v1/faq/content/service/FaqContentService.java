package com.bithumbsystems.cpc.management.api.v1.faq.content.service;

import com.bithumbsystems.cpc.management.api.v1.faq.content.mapper.FaqContentMapper;
import com.bithumbsystems.cpc.management.api.v1.faq.content.model.response.FaqContentResponse;
import com.bithumbsystems.persistence.mongodb.faq.content.model.entity.FaqContent;
import com.bithumbsystems.persistence.mongodb.faq.content.service.FaqContentDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FaqContentService {

    private final FaqContentDomainService faqDomainService;

    /**
     * 콘텐츠 모든 정보
     * @return
     */
    public Flux<FaqContentResponse> findAll() {
        return faqDomainService.findAll().map(FaqContentMapper.INSTANCE::faqContentRespone);
    }

    /**
     * 콘텐츠 1개 찾기
     * @param userId
     * @return
     */
    public Mono<FaqContentResponse> findFaqById(String userId) {
        return faqDomainService.findFaqById(userId).map(FaqContentMapper.INSTANCE::faqContentRespone);
    }

    /**
     * 콘텐츠 userId 찾기
     * @param userId
     * @return
     */
    public Mono<FaqContentResponse> findFaqByUserId(String userId) {
        return faqDomainService.findFaqByUserId(userId).map(FaqContentMapper.INSTANCE::faqContentRespone);
    }

    /**
     * 콘텐츠 1개 저장
     * @param faqContent
     * @return FaqContentResponse
     */
    public Mono<FaqContentResponse> create(FaqContent faqContent) {
        return faqDomainService.save(faqContent).map(FaqContentMapper.INSTANCE::faqContentRespone);
    }

    /**
     * 콘텐츠 업데이트
     * @param faqContent
     * @return FaqContentResponse
     */
    public Mono<FaqContentResponse> updateContent(FaqContent faqContent) {
        String userId = faqContent.getUserId();
        return faqDomainService.findFaqByUserId(userId).flatMap(c -> {
            c.setOrder(faqContent.getOrder());
            c.setTitle(faqContent.getTitle());
            c.setContent(faqContent.getContent());
            return faqDomainService.updateContent(c).map(FaqContentMapper.INSTANCE::faqContentRespone);
        });
    }

    /**
     * 콘텐츠 삭제
     * @param userId
     * @return FaqContentResponse
     */
    public Mono<Void> deleteContent(String userId) {
        return faqDomainService.findFaqByUserId(userId).flatMap(faqContent -> {
                return faqDomainService.deleteContent(faqContent);
        });
    }
}
