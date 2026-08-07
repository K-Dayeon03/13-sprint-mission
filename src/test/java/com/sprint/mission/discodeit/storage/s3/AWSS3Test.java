package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class AWSS3Test {

    private Properties properties;

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;

    private final String key = "test/aws-s3-test.txt";
    private final String content = "hello s3";

    @BeforeEach
    void setUp() {
        properties = loadProperties();
        accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        region = properties.getProperty("AWS_S3_REGION");
        bucket = properties.getProperty("AWS_S3_BUCKET");

        assumeTrue(hasText(accessKey), "AWS_S3_ACCESS_KEY is required");
        assumeTrue(hasText(secretKey), "AWS_S3_SECRET_KEY is required");
        assumeTrue(hasText(region), "AWS_S3_REGION is required");
        assumeTrue(hasText(bucket), "AWS_S3_BUCKET is required");
    }

    @Test
    void upload() {
        try (S3Client s3Client = s3Client()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(request, RequestBody.fromString(content));

            assertThat(true).isTrue();
        }
    }

    @Test
    void download() {
        try (S3Client s3Client = s3Client()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            ResponseBytes<?> response = s3Client.getObjectAsBytes(request);
            String downloaded = response.asString(StandardCharsets.UTF_8);

            assertThat(downloaded).isEqualTo(content);
        }
    }

    @Test
    void createPresignedUrl() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URL url = presigner.presignGetObject(presignRequest).url();

            assertThat(url).isNotNull();
            assertThat(url.toString()).contains(bucket);
        }
    }

    private S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    static Properties loadProperties() {
        Properties properties = new Properties();

        try (FileInputStream inputStream = new FileInputStream(Path.of(".env").toFile())) {
            properties.load(inputStream);
        } catch (IOException e) {
            return properties;
        }

        return properties;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
