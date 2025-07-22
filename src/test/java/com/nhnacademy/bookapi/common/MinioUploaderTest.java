package com.nhnacademy.bookapi.common;

import com.nhnacademy.bookapi.common.service.MinioUploader;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioUploaderTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioUploader minioUploader;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minioUploader, "bucket", bucketName);
    }

    @Test
    void deleteImage_success() throws Exception {
        String objectName = "test.jpg";

        minioUploader.deleteImage(objectName);

        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteImage_exception() throws Exception {
        String objectName = "fail.jpg";

        doThrow(new RuntimeException("삭제 실패"))
                .when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        // act
        minioUploader.deleteImage(objectName);

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }
}
