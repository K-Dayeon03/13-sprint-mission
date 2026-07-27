package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        Instant lastReadAt
) {
    public CreateReadStatusRequest(UUID userId, UUID channelId) {
        this(userId, channelId, null);
    }
}
