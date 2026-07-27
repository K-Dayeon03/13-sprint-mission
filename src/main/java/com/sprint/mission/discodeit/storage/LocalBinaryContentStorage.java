package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentDataNotFoundException;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentReadFailedException;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentStorageInitFailedException;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentWriteFailedException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new BinaryContentStorageInitFailedException(root.toString(), e);
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try {
            Files.write(resolvePath(id), bytes);
            return id;
        } catch (IOException e) {
            throw new BinaryContentWriteFailedException(id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            Path path = resolvePath(id);
            if (!Files.exists(path)) {
                throw new BinaryContentDataNotFoundException(id);
            }
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new BinaryContentReadFailedException(id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStream inputStream = get(binaryContentDto.id());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
                .contentLength(binaryContentDto.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(binaryContentDto.fileName(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(resource);
    }
}
