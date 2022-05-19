package com.bithumbsystems.persistence.mongodb.faq.content.service;

import com.bithumbsystems.persistence.mongodb.faq.content.model.entity.FaqContent;
import com.bithumbsystems.persistence.mongodb.faq.content.repository.FaqContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FaqContentDomainService {

    private final FaqContentRepository faqContentRepository;

    /**
     * 콘텐츠 모든 정보
     * @return
     */
    public Flux<FaqContent> findAll() {
        return faqContentRepository.findAll();
    }

    /**
     * 콘텐츠 1개 찾기
     * @param userId
     * @return
     */
    public Mono<FaqContent> findFaqById(String userId) {
        return faqContentRepository.findById(userId);
    }

    /**
     * 콘텐츠 userId 찾기
     * @param userId
     * @return
     */
    public Mono<FaqContent> findFaqByUserId(String userId) {
        return faqContentRepository.findByUserId(userId);
    }

    /**
     * 콘텐츠 1개 저장
     * @param faqContent
     * @return FaqContentResponse
     */
    public Mono<FaqContent> save(FaqContent faqContent) {
        return faqContentRepository.insert(faqContent);
    }

    /**
     * 콘텐츠 업데이트
     * @param faqContent
     * @return FaqContentResponse
     */
    public Mono<FaqContent> updateContent(FaqContent faqContent) {
        return faqContentRepository.save(faqContent);
    }

    /**
     * 콘텐츠 삭제
     * @param faqContent
     * @return FaqContentResponse
     */
    public Mono<Void> deleteContent(FaqContent faqContent) {
        return faqContentRepository.delete(faqContent);
    }
}
