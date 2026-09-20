package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentDataNotFoundException;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentReadFailedException;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentWriteFailedException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private static final long DEFAULT_PRESIGNED_URL_EXPIRATION_SECONDS = 600;

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final long presignedUrlExpiration;

    public S3BinaryContentStorage(String accessKey, String secretKey, String region, String bucket) {
        this(accessKey, secretKey, region, bucket, DEFAULT_PRESIGNED_URL_EXPIRATION_SECONDS);
    }

    @Autowired
    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try (S3Client s3Client = getS3Client()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(id.toString())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(bytes));
            return id;
        } catch (Exception e) {
            log.error("Failed to upload binary content to S3. binaryContentId={}, bucket={}, region={}",
                    id, bucket, region, e);
            throw new BinaryContentWriteFailedException(id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try (S3Client s3Client = getS3Client()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(id.toString())
                    .build();

            byte[] bytes = s3Client.getObject(request, ResponseTransformer.toBytes()).asByteArray();
            return new ByteArrayInputStream(bytes);
        } catch (NoSuchKeyException e) {
            throw new BinaryContentDataNotFoundException(id);
        } catch (Exception e) {
            log.error("Failed to read binary content from S3. binaryContentId={}, bucket={}, region={}",
                    id, bucket, region, e);
            throw new BinaryContentReadFailedException(id, e);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentResponse binaryContentDto) {
        String presignedUrl = generatePresignedUrl(
                binaryContentDto.id().toString(),
                binaryContentDto.contentType()
        );

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    private String generatePresignedUrl(String key, String contentType) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }
}
