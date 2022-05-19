package com.bithumbsystems.cpc.management.api.v1.file.service;

import com.bithumbsystems.cpc.management.api.v1.file.exception.FileException;
import com.bithumbsystems.cpc.management.api.core.model.enums.ErrorCode;
import com.bithumbsystems.persistence.mongodb.file.model.entity.File;
import com.bithumbsystems.persistence.mongodb.file.service.FileDomainService;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final S3AsyncClient s3AsyncClient;
    private final FileDomainService fileDomainService;

    /**
     * File save in MongoDB
     * @param info
     * @return
     */
    public Mono<File> save(File info) {
        return fileDomainService.save(info);
    }

    /**
     * 파일 정보 조회
     *
     * @param fileKey
     * @return
     */
    public Mono<File> findById(String fileKey) {
        return fileDomainService.findById(fileKey);
    }


    /**
     * s3 file download
     *
     * @param fileKey
     * @param bucketName
     * @return
     */
    public Mono<InputStream> download(String fileKey, String bucketName) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        return Mono.fromFuture(
                s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
                        .thenApply(bytes -> {
                            return bytes.asInputStream();  // .asByteArray(); // ResponseBytes::asByteArray
                        })
                        .whenComplete((res, error) -> {
                                    try {
                                        if (res != null) {
                                            log.debug("whenComplete -> {}", res);
                                        }else {
                                            error.printStackTrace();
                                        }
                                    }finally {
                                        //s3AsyncClient.close();
                                    }
                                }
                        )
        );
    }
    /**
     * S3 File Upload
     *
     * @param fileKey
     * @param fileName
     * @param fileSize
     * @param bucketName
     * @param content
     * @return
     */
    public Mono<PutObjectResponse> upload(String fileKey, String fileName, Long fileSize, String bucketName, ByteBuffer content) {  // Mono<ByteBuffer> content) {
        // String fileName = part.filename();
        log.debug("save => fileKey : " + fileKey);
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("filename", fileName);
        metadata.put("filesize", String.valueOf(fileSize));

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType((MediaType.APPLICATION_OCTET_STREAM).toString())
                .contentLength(fileSize)
                .metadata(metadata)
                .key(fileKey)
                .build();

        return Mono.fromFuture(
                s3AsyncClient.putObject(
                        objectRequest, AsyncRequestBody.fromByteBuffer(content)  // .fromPublisher(content)
                ).whenComplete((resp, err) -> {
                    try {
                        if (resp != null) {
                            log.info("upload success. Details {}", resp);
                        }else {
                            log.error("whenComplete error : {}", err);
                            err.printStackTrace();
                        }
                    }finally {
                        //s3AsyncClient.close();
                    }
                }).thenApply(res -> {
                    log.debug("putObject => {}", res);
                    return res;
                })
        );
    }

    /**
     * s3 file delete
     * @param fileKey
     * @param bucketName
     * @return
     */
    public Mono<DeleteObjectResponse> s3delete(String fileKey, String bucketName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        return Mono.fromFuture(
                s3AsyncClient
                        .deleteObject(deleteObjectRequest)
                        .whenComplete((res, err) -> {
                            try {
                                if (res != null) {
                                    log.info("delete success. Details {}", res);
                                } else {
                                    log.error("whenComplete error : {}", err);
                                    err.printStackTrace();
                                }
                            } finally {
                                //s3AsyncClient.close();
                            }
                        })
                        .thenApply(response -> {
                            return response;
                        })
        );
    }

    /**
     * delete the file info in mongo db (update)
     *
     * @param fileKey
     * @return
     */
    public Mono<File> delete(String fileKey) {
        return fileDomainService.findById(fileKey)
                .flatMap(entity -> {
                    entity.setDelYn(true);
                    entity.setDeletedAt(new Date());
                    entity.setDerId("test");
                    return fileDomainService.save(entity);
                });
    }

    /**
     * Not used
     * @param fileKey
     * @param fileName
     * @param fileSize
     * @param bucketName
     * @param content
     * @return
     */
    public Mono<Object> save(String fileKey, String fileName, Long fileSize, String bucketName, ByteBuffer content) {  // Mono<ByteBuffer> content) {
        // String fileName = part.filename();
        log.debug("save => fileKey : " + fileKey);
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("filename", fileName);
        metadata.put("filesize", String.valueOf(fileSize));

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType((MediaType.APPLICATION_OCTET_STREAM).toString())
                .contentLength(fileSize)
                .metadata(metadata)
                .key(fileKey)
                .build();

        return Mono.fromFuture(
                s3AsyncClient.putObject(
                        objectRequest, AsyncRequestBody.fromByteBuffer(content)  // .fromPublisher(content)
                ).whenComplete((resp, err) -> {
                    try {
                        if (resp != null) {
                            log.info("upload success. Details {}", resp);
                        }else {
                            log.error("whenComplete error : {}", err);
                            err.printStackTrace();
                        }
                    }finally {
                        s3AsyncClient.close();
                    }
                }).thenApply(res -> {
                    log.debug("thenApply => {}", res);
                    log.debug("tag => {}", res.eTag());
                    if (!StringUtils.isEmpty(res.eTag())) {
                        log.debug("etag is not null --- execute....");
                        File info = File.builder()
                                .fileKey(fileKey)
                                .fileName(fileName)
                                .createdAt(new Date())
                                .createdId("test")
                                .delYn(false)
                                .build();
                        log.debug("file info => {}", info);

                      return fileDomainService.save(info);
                    } else {
                      throw new FileException(ErrorCode.FAIL_SAVE_FILE);
                    }
                })
        );

    }
}
