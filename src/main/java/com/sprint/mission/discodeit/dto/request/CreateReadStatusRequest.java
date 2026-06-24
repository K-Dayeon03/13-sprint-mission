package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
    public CreateReadStatusRequest(UUID userId, UUID channelId) {
        this(userId, channelId, null);
    }
}
