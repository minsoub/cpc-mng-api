package com.bithumbsystems.cpc.management.api.v1.file.controller;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.bithumbsystems.cpc.management.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.management.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.management.api.v1.file.service.FileService;
import com.bithumbsystems.persistence.mongodb.file.model.entity.File;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File Test APIs", description = "File Test APIs for demo purpose")
public class FileController {

    private final FileService fileService;
    private final AwsProperties awsProperties;

    @PostMapping(value = "/upload/s3", consumes = MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> s3upload(@RequestHeader HttpHeaders headers,  @RequestPart("files") Mono<FilePart> filePart) {

        String fileKey = UUID.randomUUID().toString();

        MediaType mediaType = headers.getContentType();

        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        AtomicReference<String> fileName = new AtomicReference<>();
        AtomicReference<Long> fileSize = new AtomicReference<>();

        return filePart.doOnNext(part -> {
                    log.debug("file name => " + part.filename());
                    fileName.set(part.filename());
                })
                //        .map(part ->part.content().concatMap(dataBuffer->{ return dataBuffer.asByteBuffer();}))
               .map(Part::content)
               .log()
               .flatMap(data -> {
                   log.debug("Here is ....");
                   return DataBufferUtils.join(data)
                                    .flatMap(dataBuffer -> {
                                        log.debug("dataBuffer join...");
                                        ByteBuffer buf = dataBuffer.asByteBuffer();
                                        log.debug("byte size ===> " + buf.array().length);

                                        fileSize.set((long) buf.array().length); // dataBuffer.readableByteCount());

                                        return fileService.upload(fileKey, fileName.toString(), fileSize.get(), awsProperties.getBucket(), buf)
                                                .flatMap(res -> {
                                                    log.debug("service upload res => {}", res);
                                                    File info = File.builder()
                                                            .fileKey(fileKey)
                                                            .fileName(fileName.toString())
                                                            .createdAt(new Date())
                                                            .createdId("test")
                                                            .delYn(false)
                                                            .build();
                                                    return fileService.save(info);
                                                });
                                    });
                   })
               .log()
               .map(res -> {
                   log.debug("=========res => {}", res);
                   return ResponseEntity.ok().body(new SingleResponse(res));
               });
    }

    @GetMapping(value = "/download/s3/{fileKey}", produces = APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<?>> s3upload(@PathVariable String fileKey) {

        AtomicReference<String> fileName = new AtomicReference<>();

        return fileService.findById(fileKey)
                 .flatMap(res -> {
                                log.debug("find file => {}", res);
                                fileName.set(res.getFileName());
                                // s3에서 파일을 다운로드 받는다.
                                return fileService.download(fileKey, awsProperties.getBucket());
                 })
                .log()
                .map(inputStream -> {
                    log.debug("finaly result...here");
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentDispositionFormData(fileName.toString(), fileName.toString());
                    ResponseEntity<?> entity = ResponseEntity.ok().cacheControl(CacheControl.noCache())
                            .headers(headers)
                            .body(new InputStreamResource(inputStream));
                    return entity;
                });
    }

    @DeleteMapping("/delete/s3/{fileKey}")
    public Mono<ResponseEntity<?>> s3delete(@PathVariable String fileKey) {
        return fileService.findById(fileKey)
                .flatMap(res -> {
                    log.debug("find file => {}", res);
                    return fileService.s3delete(fileKey, awsProperties.getBucket())
                            .flatMap(deleteObjectResponse -> {
                                log.debug("service delete called..");
                                return fileService.delete(fileKey);
                            });
                })
                .log()
                .map(result -> {
                    SingleResponse<?> response = new SingleResponse(result);
                    log.debug("response => {}", response);
                    return ResponseEntity.ok().body(response);
                });
    }

    Mono<byte[]> mergeDataBuffers(Flux<DataBuffer> dataBufferFlux) {
        return DataBufferUtils.join(dataBufferFlux)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                });
    }
}
