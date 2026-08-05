package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
        //다운로드 API가 따로있어서 일반 조회 DTO에는 bytes를 뻄
        UUID id,
        Instant createdAt,
        String fileName,
        String contentType,
        long size,
        String downloadUrl
) {}
