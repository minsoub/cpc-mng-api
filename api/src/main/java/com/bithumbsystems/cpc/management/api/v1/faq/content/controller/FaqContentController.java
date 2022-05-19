package com.bithumbsystems.cpc.management.api.v1.faq.content.controller;

import com.bithumbsystems.cpc.management.api.v1.faq.content.model.response.FaqContentResponse;
import com.bithumbsystems.cpc.management.api.v1.faq.content.service.FaqContentService;
import com.bithumbsystems.persistence.mongodb.faq.content.model.entity.FaqContent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("lrc")
public class FaqContentController {

    private final FaqContentService faqContentService;

    /**
     * 콘텐츠 모든 정보
     * @return
     */
    @GetMapping(value = "/faq_content")
    public Flux<FaqContentResponse> getAllContent() {
        return faqContentService.findAll();
    }

    /**
     * 콘텐츠 1개 찾기
     * @param userId
     * @return
     */
    @GetMapping("/faq_content/{userId}")
    public Mono<ResponseEntity<FaqContentResponse>> getContent(@PathVariable("userId") String userId) {

        return faqContentService.findFaqByUserId(userId).flatMap(c -> {
            return faqContentService.findFaqById(c.getId()).map(res -> {
                return ResponseEntity.ok(res);
            }).defaultIfEmpty(
                    new ResponseEntity<>(HttpStatus.NOT_FOUND)
            );
        });
    }

    /**
     * 콘텐츠 1개 저장
     * @param faqContent
     * @return FaqContentResponse
     */
    @PostMapping("/faq_content")
    public Mono<ResponseEntity<FaqContentResponse>> createContent(@RequestBody FaqContent faqContent) {
        return faqContentService.create(faqContent).map(c -> {
            return new ResponseEntity<FaqContentResponse>(c, HttpStatus.CREATED);
        }).defaultIfEmpty(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    /**
     * 콘텐츠 업데이트
     * @param faqContent
     * @return FaqContentResponse
     */
    @PutMapping("/faq_content")
    public Mono<ResponseEntity<FaqContentResponse>> updateContent(@RequestBody FaqContent faqContent) {
        return faqContentService.updateContent(faqContent).map(c -> {
            return ResponseEntity.ok(c);
        }).defaultIfEmpty(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    /**
     * 콘텐츠 삭제
     * @param userId
     * @return FaqContentResponse
     */
    @DeleteMapping("/faq_content/{userId}")
    public Mono<ResponseEntity<Void>> deleteContent(@PathVariable("userId") String userId) {
        return faqContentService.deleteContent(userId).then(
                Mono.just(new ResponseEntity<Void>(HttpStatus.OK))
        ).defaultIfEmpty(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }
}
