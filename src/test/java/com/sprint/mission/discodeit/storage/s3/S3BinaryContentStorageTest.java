package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;
    private UUID binaryContentId;
    private byte[] bytes;

    @BeforeEach
    void setUp() {
        Properties properties = AWSS3Test.loadProperties();

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String region = properties.getProperty("AWS_S3_REGION");
        String bucket = properties.getProperty("AWS_S3_BUCKET");
        String presignedUrlExpirationValue = properties.getProperty("AWS_S3_PRESIGNED_URL_EXPIRATION", "600");

        assumeTrue(isAwsS3TestEnabled(properties), "AWS_S3_TEST_ENABLED=true is required");
        assumeTrue(hasUsableText(accessKey), "AWS_S3_ACCESS_KEY is required");
        assumeTrue(hasUsableText(secretKey), "AWS_S3_SECRET_KEY is required");
        assumeTrue(hasUsableText(region), "AWS_S3_REGION is required");
        assumeTrue(hasUsableText(bucket), "AWS_S3_BUCKET is required");
        assumeTrue(hasUsableText(presignedUrlExpirationValue), "AWS_S3_PRESIGNED_URL_EXPIRATION is required");

        long presignedUrlExpiration = Long.parseLong(presignedUrlExpirationValue);

        storage = new S3BinaryContentStorage(
                accessKey,
                secretKey,
                region,
                bucket,
                presignedUrlExpiration
        );
        binaryContentId = UUID.randomUUID();
        bytes = "hello s3 storage".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void put_uploadsBytes() {
        UUID result = storage.put(binaryContentId, bytes);

        assertThat(result).isEqualTo(binaryContentId);
    }

    @Test
    void get_downloadsUploadedBytes() throws IOException {
        storage.put(binaryContentId, bytes);

        try (InputStream inputStream = storage.get(binaryContentId)) {
            String downloaded = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(downloaded).isEqualTo("hello s3 storage");
        }
    }

    @Test
    void download_redirectsToPresignedUrl() {
        storage.put(binaryContentId, bytes);
        BinaryContentDto binaryContent = new BinaryContentDto(
                binaryContentId,
                Instant.now(),
                "aws-s3-test.txt",
                "text/plain",
                bytes.length,
                null
        );

        ResponseEntity<?> response = storage.download(binaryContent);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION))
                .contains(binaryContentId.toString())
                .contains("X-Amz-Signature");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean hasUsableText(String value) {
        return hasText(value) && !isPlaceholder(value);
    }

    private static boolean isPlaceholder(String value) {
        String trimmedValue = value.trim();

        return trimmedValue.contains("${");
    }

    private static boolean isAwsS3TestEnabled(Properties properties) {
        String value = System.getenv("AWS_S3_TEST_ENABLED");

        if (!hasText(value)) {
            value = properties.getProperty("AWS_S3_TEST_ENABLED");
        }

        return "true".equalsIgnoreCase(value);
    }
}
