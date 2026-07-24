package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        Instant createdAt,
        String fileName,
        String contentType,
        long size,
        byte[] bytes,
        String downloadUrl
) {
    public static BinaryContentDto from(BinaryContent binaryContent) {
        if (binaryContent == null) {
            return null;
        }
        return new BinaryContentDto(
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
