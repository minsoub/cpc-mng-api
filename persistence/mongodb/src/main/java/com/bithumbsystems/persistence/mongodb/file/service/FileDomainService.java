package com.bithumbsystems.persistence.mongodb.file.service;

import com.bithumbsystems.persistence.mongodb.file.model.entity.File;
import com.bithumbsystems.persistence.mongodb.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileDomainService {

    private final FileRepository fileRepository;

    /**
     * File save in MongoDB
     * @param info
     * @return
     */
    public Mono<File> save(File info) {
        return fileRepository.save(info);
    }

    /**
     * 파일 정보 조회
     *
     * @param fileKey
     * @return
     */
    public Mono<File> findById(String fileKey) {
        return fileRepository.findById(fileKey);
    }


}
