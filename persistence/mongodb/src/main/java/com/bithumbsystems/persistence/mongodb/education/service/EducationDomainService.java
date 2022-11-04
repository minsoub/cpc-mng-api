package com.bithumbsystems.persistence.mongodb.education.service;

import com.bithumbsystems.persistence.mongodb.education.model.entity.Education;
import com.bithumbsystems.persistence.mongodb.education.repository.EducationCustomRepository;
import com.bithumbsystems.persistence.mongodb.education.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationDomainService {
    private final EducationCustomRepository educationCustomRepository;
    private final EducationRepository educationRepository;

    /**
     * 신청자 관리 조회
     *
     * @param startDate
     * @param endDate
     * @param keyword
     * @param isAnswerComplete
     * @return
     */
    public Flux<Education> findBySearchText(LocalDate startDate, LocalDate endDate, String keyword, Boolean isAnswerComplete) {
        return educationCustomRepository.findBySearchText(startDate, endDate, keyword, isAnswerComplete);
    }

    /**
     * 신청자 정보 수정
     * @param education
     * @return
     */
    public Mono<Education> updateEducation(Education education) {
        return educationRepository.save(education);
    }

    /**
     * 신청장 관리 상세 데이터 조회
     * @param id
     * @return
     */
    public Mono<Education> findById(String id) {
        return educationRepository.findById(id);
    }
}
