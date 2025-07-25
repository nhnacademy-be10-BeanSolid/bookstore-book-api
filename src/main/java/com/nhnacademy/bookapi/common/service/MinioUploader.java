package com.nhnacademy.bookapi.common.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioUploader {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucket;

    public String upload(InputStream in, Long bookId, String extension) {
        try {
            String objectName = bookId + "-" + UUID.randomUUID() + extension;
            String contentType = getContentTypeByExtension(extension);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(in, -1, 10L * 1024 * 1024)
                            .contentType(contentType)
                            .build()
            );

            return "/images/book/" + objectName;

        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    public String uploadFromUrl(Long bookId, String imageUrl) {
        // 확장자 추출
        String extension = "";
        int lastDotIndex = imageUrl.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < imageUrl.length() - 1) {
            extension = imageUrl.substring(lastDotIndex);
        } else {
            extension = ".jpg"; // 기본 확장자
        }

        try (InputStream in = new URL(imageUrl).openStream()) {
            return upload(in, bookId, extension);
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    public void deleteImage(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("이미지 삭제 실패: {}", objectName, e);
        }
    }

    public String getPresignedUrl(String objectName) {
        String image = extractObjectName(objectName);
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(image)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Presigned URL 생성 실패", e);
        }
    }

    public String extractObjectName(String imageUrl) {
        if (imageUrl == null) {
            return null;
        }

        String prefix = "/images/book/";
        String pathPart = imageUrl;

        if (imageUrl.startsWith(prefix)) {
            pathPart = imageUrl.substring(prefix.length());
        }

        // 쿼리 파라미터 시작 위치 찾기 ('?' 또는 '&')
        int queryIndex = pathPart.indexOf('?');
        if (queryIndex == -1) {
            queryIndex = pathPart.indexOf('&');
        }

        if (queryIndex != -1) {
            pathPart = pathPart.substring(0, queryIndex);
        }

        return pathPart;
    }

    private String getContentTypeByExtension(String extension) {
        if (extension == null) {
            return "application/octet-stream";
        }

        return switch (extension.toLowerCase()) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".bmp" -> "image/bmp";
            case ".webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
