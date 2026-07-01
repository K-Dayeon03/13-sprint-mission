package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        Instant createdAt,
        String fileName,
        String contentType,
        long size,
        byte[] bytes,
        String downloadUrl
) {
    public static BinaryContentResponse from(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getBytes().length,
                binaryContent.getBytes(),
                "/api/binary-contents/" + binaryContent.getId()
        );
    }
}
